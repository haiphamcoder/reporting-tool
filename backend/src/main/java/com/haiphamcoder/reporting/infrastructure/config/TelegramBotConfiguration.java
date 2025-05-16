package com.haiphamcoder.reporting.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.haiphamcoder.reporting.infrastructure.config.properties.TelegramBotProperties;

@Configuration
public class TelegramBotConfiguration {

    @Bean("telegramBotProperties")
    @ConfigurationProperties(prefix = "telegram.bot")
    TelegramBotProperties telegramBotProperties() {
        return new TelegramBotProperties();
    }
}
