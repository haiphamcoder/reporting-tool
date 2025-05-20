package com.haiphamcoder.authentication.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.haiphamcoder.authentication.security.JwtAuthenticationFilter;
import com.haiphamcoder.authentication.security.UsernamePasswordBodyAuthenticationFilter;
import com.haiphamcoder.authentication.security.oauth2.CustomOAuth2UserService;
import com.haiphamcoder.authentication.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.haiphamcoder.authentication.security.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.haiphamcoder.authentication.security.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.haiphamcoder.authentication.service.LogoutService;
import com.haiphamcoder.authentication.shared.UnauthorizedAuthenticationEntryPoint;

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
                        "/register",
                        "/authenticate",
                        "/oauth2/authorization/**",
                        "/oauth2/callback/**",
        };

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final UnauthorizedAuthenticationEntryPoint unauthorizedAuthenticationEntryPoint;
        private final LogoutService logoutHandler;
        private final CustomOAuth2UserService customOAuth2UserService;
        private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
        private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
        private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(unauthorizedAuthenticationEntryPoint))
                                .authorizeHttpRequests(request -> request
                                                .requestMatchers(AUTH_WHITELIST)
                                                .permitAll()
                                                .anyRequest()
                                                .authenticated())
                                .oauth2Login(oauth2 -> oauth2
                                                .authorizationEndpoint(authorization -> authorization
                                                                .baseUri("/oauth2/authorization")
                                                                .authorizationRequestRepository(
                                                                                httpCookieOAuth2AuthorizationRequestRepository))
                                                .redirectionEndpoint(redirection -> redirection
                                                                .baseUri("/oauth2/callback/*"))
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userService(customOAuth2UserService))
                                                .successHandler(oAuth2AuthenticationSuccessHandler)
                                                .failureHandler(oAuth2AuthenticationFailureHandler))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtAuthenticationFilter,
                                                UsernamePasswordBodyAuthenticationFilter.class)
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .addLogoutHandler(logoutHandler));
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
