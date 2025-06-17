package com.haiphamcoder.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

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
                                .route("integrated-service", r -> r.path("/integrated/**")
                                                .filters(f -> f.stripPrefix(1))
                                                .uri("lb://integrated-service"))
                                .build();
        }

        @Bean
        public CorsWebFilter corsWebFilter() {
                CorsConfiguration corsConfig = new CorsConfiguration();
                corsConfig.setAllowedOrigins(
                                Arrays.asList("http://localhost:3000", "http://localhost:5173",
                                                "http://reporting-tool.site", "http://localhost"));
                corsConfig.setMaxAge(3600L);
                corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                corsConfig.setAllowedHeaders(Arrays.asList("*"));
                corsConfig.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", corsConfig);

                return new CorsWebFilter(source);
        }
}
