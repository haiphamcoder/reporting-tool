package com.haiphamcoder.integrated.service;

import com.haiphamcoder.integrated.domain.model.TelegramMessage;

public interface TelegramBot {

    void sendMessage(String chatId, String content, TelegramMessage.ParseMode parseMode);

}