package com.haiphamcoder.reporting.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.haiphamcoder.reporting.domain.model.Permission;
import com.haiphamcoder.reporting.domain.model.Role;
import com.haiphamcoder.reporting.security.JwtAuthenticationFilter;
import com.haiphamcoder.reporting.shared.UnauthorizedAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfiguration {

        @Value("${endpoints.cors.allowed-origins}")
        private String allowedOrigins;

        public static final String[] AUTH_WHITELIST = {
                        "/",
                        "/error/**",
                        "/favicon.ico",
                        "/api/v1/auth/register",
                        "/api/v1/auth/authenticate",
                        "/api/v1/sse/subscribe/**",
                        "/api/v1/sse/publish/**",
                        "/oauth2/authorization/**",
                        "/login/oauth2/code/**",
                        "/oauth2/callback/**",
                        "/api/docs/**",
                        "/swagger-ui/**",
        };

        private static final String ADMIN_ENDPOINT = "/api/v1/admin/**";

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final UnauthorizedAuthenticationEntryPoint unauthorizedAuthenticationEntryPoint;

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(unauthorizedAuthenticationEntryPoint))
                                .authorizeHttpRequests(request -> request
                                                .requestMatchers(AUTH_WHITELIST)
                                                .permitAll()
                                                .requestMatchers(ADMIN_ENDPOINT).hasAnyRole(Role.ADMIN.name())
                                                .requestMatchers(HttpMethod.GET, ADMIN_ENDPOINT)
                                                .hasAnyAuthority(Permission.ADMIN_READ.name())
                                                .requestMatchers(HttpMethod.POST, ADMIN_ENDPOINT)
                                                .hasAnyAuthority(Permission.ADMIN_CREATE.name())
                                                .requestMatchers(HttpMethod.PUT, ADMIN_ENDPOINT)
                                                .hasAnyAuthority(Permission.ADMIN_UPDATE.name())
                                                .requestMatchers(HttpMethod.DELETE, ADMIN_ENDPOINT)
                                                .hasAnyAuthority(Permission.ADMIN_DELETE.name())
                                                .anyRequest()
                                                .authenticated())
                                .addFilter(jwtAuthenticationFilter);
                return http.build();
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins.split(",")));
                configuration.setAllowedMethods(Arrays.asList("*"));
                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setAllowCredentials(true);
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
