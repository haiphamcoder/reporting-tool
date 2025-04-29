package com.haiphamcoder.cdp.application.service;

import com.haiphamcoder.cdp.domain.model.TelegramMessage;

public interface TelegramBot {

    void sendMessage(String chatId, String content, TelegramMessage.ParseMode parseMode);
    
} 