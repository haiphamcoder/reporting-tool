package com.haiphamcoder.dataprocessing.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfiguration {

    @Value("${grpc.reporting-service.host:localhost}")
    private String reportingServiceHost;

    @Value("${source.grpc.source-service.port:9091}")
    private int sourceServicePort;

    @Value("${chart.grpc.chart-service.port:9092}")
    private int chartServicePort;

    @Bean("sourceServiceChannel")
    public ManagedChannel sourceServiceChannel() {
        return ManagedChannelBuilder.forAddress(reportingServiceHost, sourceServicePort)
                .usePlaintext()
                .build();
    }

    @Bean("chartServiceChannel")
    public ManagedChannel chartServiceChannel() {
        return ManagedChannelBuilder.forAddress(reportingServiceHost, chartServicePort)
                .usePlaintext()
                .build();
    }
}
