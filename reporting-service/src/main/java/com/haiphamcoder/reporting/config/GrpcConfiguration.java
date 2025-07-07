package com.haiphamcoder.reporting.config;

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

    @Value("${grpc.data-processing-service.host:localhost}")
    private String dataProcessingServiceHost;

    @Value("${grpc.data-processing-service.port:9094}")
    private int dataProcessingServicePort;

    @Bean(name = "userManagementServiceChannel")
    ManagedChannel userManagementServiceChannel() {
        return ManagedChannelBuilder.forAddress(userManagementServiceHost, userManagementServicePort)
                .usePlaintext()
                .build();
    }

    @Bean(name = "dataProcessingServiceChannel")
    ManagedChannel dataProcessingServiceChannel() {
        return ManagedChannelBuilder.forAddress(dataProcessingServiceHost, dataProcessingServicePort)
                .usePlaintext()
                .build();
    }
}