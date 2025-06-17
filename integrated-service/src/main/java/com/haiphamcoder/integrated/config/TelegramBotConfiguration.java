package com.haiphamcoder.integrated.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.haiphamcoder.integrated.config.properties.TelegramBotProperties;

@Configuration
public class TelegramBotConfiguration {

    @Bean("telegramBotProperties")
    @ConfigurationProperties(prefix = "telegram.bot")
    TelegramBotProperties telegramBotProperties() {
        return new TelegramBotProperties();
    }
}
