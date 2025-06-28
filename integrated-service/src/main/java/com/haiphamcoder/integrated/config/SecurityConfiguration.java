package com.haiphamcoder.integrated.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.haiphamcoder.integrated.security.JwtAuthenticationFilter;
import com.haiphamcoder.integrated.security.UnauthorizedAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfiguration {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final UnauthorizedAuthenticationEntryPoint unauthorizedAuthenticationEntryPoint;

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.disable())
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(unauthorizedAuthenticationEntryPoint))
                                .authorizeHttpRequests(request -> request
                                                .anyRequest()
                                                .authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }
}
