package com.haiphamcoder.usermanagement.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfiguration {

    @Value("${grpc.integrated-service.host:localhost}")
    private String integratedServiceHost;

    @Value("${email.grpc.integrated-service.port:9093}")
    private int emailIntegratedServicePort;

    @Bean(name = "emailIntegratedServiceChannel")
    ManagedChannel emailIntegratedServiceChannel() {
        return ManagedChannelBuilder.forAddress(integratedServiceHost, emailIntegratedServicePort)
                .usePlaintext()
                .build();
    }
}