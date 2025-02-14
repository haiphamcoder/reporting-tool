package com.haiphamcoder.cdp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.haiphamcoder.cdp.application.service.AuthenticationService;
import com.haiphamcoder.cdp.domain.model.AuthenticationResponse;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@Slf4j
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(
			AuthenticationService authenticationService) {
		return args -> {
			AuthenticationResponse response = authenticationService.createAdminUser();
			if (response == null) {
				log.info("Admin user already exists");
			} else {
				log.info("Admin token: {}", response.getAccessToken());
			}
		};
	}

}
