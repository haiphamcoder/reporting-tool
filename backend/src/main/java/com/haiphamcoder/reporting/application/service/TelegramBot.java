package com.haiphamcoder.reporting.application.service;

import com.haiphamcoder.reporting.domain.model.TelegramMessage;

public interface TelegramBot {

    void sendMessage(String chatId, String content, TelegramMessage.ParseMode parseMode);

}