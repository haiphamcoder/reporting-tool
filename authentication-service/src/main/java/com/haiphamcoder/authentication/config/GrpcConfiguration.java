package com.haiphamcoder.authentication.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfiguration {

    @Value("${grpc.user-management-service.host:localhost}")
    private String userManagementServiceHost;

    @Value("${grpc.user-management-service.port:9090}")
    private int userManagementServicePort;

    @Bean("userManagementServiceChannel")
    public ManagedChannel userManagementServiceChannel() {
        return ManagedChannelBuilder.forAddress(userManagementServiceHost, userManagementServicePort)
                .usePlaintext()
                .build();
    }
}