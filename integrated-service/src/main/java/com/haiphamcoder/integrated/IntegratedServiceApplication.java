package com.haiphamcoder.integrated;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.haiphamcoder.integrated.config.properties.TelegramBotProperties;
import com.haiphamcoder.integrated.service.TelegramBot;
import com.haiphamcoder.integrated.service.impl.TelegramBotImpl;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
public class IntegratedServiceApplication implements CommandLineRunner {

	private final TelegramBotProperties telegramBotProperties;
	private final TelegramBotsLongPollingApplication botsApplication;
	private final TelegramBot telegramBot;
	private final boolean telegramBotEnabled;

	public IntegratedServiceApplication(@Qualifier("telegramBotProperties") TelegramBotProperties telegramBotProperties,
			@Value("${telegram.bot.enabled}") boolean telegramBotEnabled,
			TelegramBot telegramBot) {
		this.telegramBotProperties = telegramBotProperties;
		this.botsApplication = new TelegramBotsLongPollingApplication();
		this.telegramBot = telegramBot;
		this.telegramBotEnabled = telegramBotEnabled;
	}

	@Override
	public void run(String... args) throws Exception {
		if (telegramBotEnabled) {
			registerBot();
		}
	}

	private void registerBot() {
		try {
			botsApplication.registerBot(telegramBotProperties.getToken(), (TelegramBotImpl) telegramBot);
		} catch (TelegramApiException e) {
			log.error("Error while registering bot: ", e);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(IntegratedServiceApplication.class, args);
	}

}
