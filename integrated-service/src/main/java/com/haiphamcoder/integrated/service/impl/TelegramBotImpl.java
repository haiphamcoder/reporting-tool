package com.haiphamcoder.integrated.service.impl;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import com.haiphamcoder.integrated.service.TelegramBot;
import com.haiphamcoder.integrated.domain.model.TelegramMessage.ParseMode;
import com.haiphamcoder.integrated.config.properties.TelegramBotProperties;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TelegramBotImpl implements LongPollingSingleThreadUpdateConsumer, TelegramBot {
    private final TelegramClient telegramClient;
    private final String botUsername;

    private static final String COMMAND_PREFIX = "/";
    private final Map<Long, CompletableFuture<Void>> userProcessingStatus = new ConcurrentHashMap<>();

    public TelegramBotImpl(@Qualifier("telegramBotProperties") TelegramBotProperties telegramBotProperties) {
        this.telegramClient = new OkHttpTelegramClient(telegramBotProperties.getToken());
        this.botUsername = telegramBotProperties.getUsername();
    }

    @Override
    public void sendMessage(String chatId, String content, ParseMode parseMode) {
        SendMessage message = new SendMessage(chatId, content);
        message.setParseMode(parseMode.getValue());
        try {
            telegramClient.execute(message);
            log.info("Message sent successfully to chatId: {}", chatId);
        } catch (TelegramApiException e) {
            log.error("Error sending message to chatId: {}", chatId, e);
        }
    }

    @Override
    public void consume(Update update) {
        try {
            if (update.hasMessage()) {
                Message message = update.getMessage();
                Long chatId = message.getChatId();
                User user = message.getFrom();

                CompletableFuture<Void> processingFuture = new CompletableFuture<>();
                userProcessingStatus.put(user.getId(), processingFuture);

                try {
                    if (chatId < 0) {
                        // Group chat
                        handleGroupChat(String.valueOf(chatId), message, user);
                    } else {
                        // Private chat
                        handlePrivateChat(String.valueOf(chatId), message, user);
                    }
                } catch (Exception e) {
                    log.error("Error processing message: {}", message, e);
                    sendMessage(String.valueOf(chatId), "Xin lỗi, đã có lỗi xảy ra khi xử lý yêu cầu của bạn.",
                            ParseMode.MARKDOWN);
                    processingFuture.completeExceptionally(e);
                } finally {
                    userProcessingStatus.remove(user.getId());
                    processingFuture.complete(null);
                }
            }
        } catch (Exception e) {
            log.error("Error processing update: {}", update, e);
        }
    }

    private void handleGroupChat(String chatId, Message message, User user) {
        if (message.hasText()) {
            String messageText = message.getText();
            if (isMentioned(messageText)) {
                // Remove @botUsername from messageText
                messageText = messageText.replace("@" + botUsername, "").trim();
                handleTextMessage(chatId, messageText, user);
            }
        }
    }

    private void handlePrivateChat(String chatId, Message message, User user) {
        if (message.hasText()) {
            String messageText = message.getText();
            if (messageText.startsWith(COMMAND_PREFIX)) {
                handleCommand(chatId, messageText, user);
            } else {
                handleTextMessage(chatId, messageText, user);
            }
        } else if (message.hasPhoto()) {
            handlePhotoMessage(chatId, message, user);
        } else if (message.hasDocument()) {
            handleDocumentMessage(chatId, message, user);
        }
    }

    private boolean isMentioned(String messageText) {
        String[] parts = messageText.split(" ");
        for (String part : parts) {
            if (part.startsWith("@")) {
                return part.equals("@" + botUsername);
            }
        }
        return false;
    }

    private void handleCommand(String chatId, String command, User user) {
        String commandName = command.substring(1).split("\\s+")[0].toLowerCase();
        String[] args = command.split("\\s+");

        switch (commandName) {
            case "start":
                sendMessage(chatId,
                        String.format(
                                "Xin chào %s! Tôi là trợ lý Telegram của bạn. Tôi có thể giúp gì cho bạn hôm nay?",
                                user.getFirstName()),
                        ParseMode.MARKDOWN);
                break;
            case "help":
                sendMessage(chatId,
                        "Các lệnh có sẵn:\n" +
                                "/start - Bắt đầu sử dụng bot\n" +
                                "/help - Hiển thị thông tin trợ giúp\n" +
                                "/echo <tin nhắn> - Lặp lại tin nhắn của bạn",
                        ParseMode.MARKDOWN);
                break;
            case "echo":
                if (args.length > 1) {
                    String echoMessage = command.substring(6); // Remove "/echo "
                    sendMessage(chatId, echoMessage, ParseMode.MARKDOWN);
                } else {
                    sendMessage(chatId, "Vui lòng nhập tin nhắn để lặp lại", ParseMode.MARKDOWN);
                }
                break;
            default:
                sendMessage(chatId, "Lệnh không hợp lệ. Gõ /help để xem các lệnh có sẵn.", ParseMode.MARKDOWN);
        }
    }

    private void handleTextMessage(String chatId, String messageText, User user) {
        String response = String.format("Xin chào %s! Tôi đã nhận được tin nhắn của bạn: %s",
                user.getFirstName(), messageText);
        sendMessage(chatId, response, ParseMode.MARKDOWN);
    }

    private void handlePhotoMessage(String chatId, Message message, User user) {
        String response = String.format("Xin chào %s! Tôi đã nhận được ảnh của bạn. Cảm ơn bạn đã chia sẻ!",
                user.getFirstName());
        sendMessage(chatId, response, ParseMode.MARKDOWN);
    }

    private void handleDocumentMessage(String chatId, Message message, User user) {
        String response = String.format("Xin chào %s! Tôi đã nhận được tài liệu của bạn: %s",
                user.getFirstName(), message.getDocument().getFileName());
        sendMessage(chatId, response, ParseMode.MARKDOWN);
    }

}
