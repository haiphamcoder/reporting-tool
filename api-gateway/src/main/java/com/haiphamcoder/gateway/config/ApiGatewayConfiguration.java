package com.haiphamcoder.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.haiphamcoder.gateway.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ApiGatewayConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-management-service", r -> r.path("/user-management/**")
                        .filters(f -> f.stripPrefix(1).filter((GatewayFilter) jwtAuthenticationFilter))
                        .uri("lb://user-management-service"))
                .build();
    }

}
