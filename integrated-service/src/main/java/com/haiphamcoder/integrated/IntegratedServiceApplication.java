package com.haiphamcoder.integrated;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class IntegratedServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntegratedServiceApplication.class, args);
	}

}
