package com.haiphamcoder.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ApiGatewayConfiguration {

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-management-service", r -> r.path("/user-management/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://user-management-service"))
                .route("authentication-service", r -> r.path("/authentication/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://authentication-service"))
                .route("reporting-service", r -> r.path("/reporting/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://reporting-service"))
                .route("data-processing-service", r -> r.path("/data-processing/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://data-processing-service"))
                // .route("storage-service", r -> r.path("/storage/**")
                //         .filters(f -> f.stripPrefix(1))
                //         .uri("lb://storage-service"))
                .route("integrated-service", r -> r.path("/integrated/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://integrated-service"))
                .build();
    }

}
