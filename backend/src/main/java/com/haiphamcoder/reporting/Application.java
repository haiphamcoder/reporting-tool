package com.haiphamcoder.reporting;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.haiphamcoder.reporting.application.service.AuthenticationService;
import com.haiphamcoder.reporting.application.service.TelegramBot;
import com.haiphamcoder.reporting.application.service.impl.TelegramBotImpl;
import com.haiphamcoder.reporting.infrastructure.config.properties.TelegramBotProperties;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@Slf4j
public class Application implements CommandLineRunner{

	private final AuthenticationService authenticationService;
	private final TelegramBotProperties telegramBotProperties;
	private final TelegramBotsLongPollingApplication botsApplication;
	private final TelegramBot telegramBot;

	public Application(AuthenticationService authenticationService,
					   @Qualifier("telegramBotProperties") TelegramBotProperties telegramBotProperties,
					   TelegramBot telegramBot) {
		this.authenticationService = authenticationService;
		this.telegramBotProperties = telegramBotProperties;
		this.botsApplication = new TelegramBotsLongPollingApplication();
		this.telegramBot = telegramBot;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		createAdminUser();
		registerBot();
	}

	private void createAdminUser() {
		if (authenticationService.createAdminUser()) {
			log.info("Admin user created successfully");
		} else {
			log.info("Admin user already exists");
		}
	}

	private void registerBot() {
		try {
            botsApplication.registerBot(telegramBotProperties.getToken(), (TelegramBotImpl) telegramBot);
        } catch (TelegramApiException e) {
            log.error("Error while registering bot: ", e);
        }
	}
}
