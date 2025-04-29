package com.haiphamcoder.cdp.application.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import com.haiphamcoder.cdp.application.service.TelegramBot;
import com.haiphamcoder.cdp.domain.model.TelegramMessage.ParseMode;
import com.haiphamcoder.cdp.infrastructure.config.properties.TelegramBotProperties;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TelegramBotImpl implements LongPollingSingleThreadUpdateConsumer, TelegramBot{
    private final TelegramClient telegramClient;

    public TelegramBotImpl(@Qualifier("telegramBotProperties") TelegramBotProperties telegramBotProperties) {
        this.telegramClient = new OkHttpTelegramClient(telegramBotProperties.getToken());
    }

    @Override
    public void sendMessage(String chatId, String content, ParseMode parseMode) {
        SendMessage message = new SendMessage(chatId, content);
        message.setParseMode(parseMode.getValue());
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending message to chatId: {}", chatId, e);
        }
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String messageText = update.getMessage().getText();

            log.info("Received message from chatId: {}, message: {}", chatId, messageText);
            sendMessage(chatId, "Hello, how are you?", ParseMode.MARKDOWN);
        }
    }
    
}
