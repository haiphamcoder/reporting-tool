package com.haiphamcoder.cdp.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import com.haiphamcoder.cdp.domain.model.Permission;
import com.haiphamcoder.cdp.domain.model.Role;
import com.haiphamcoder.cdp.infrastructure.security.UsernamePasswordBodyAuthenticationFilter;
import com.haiphamcoder.cdp.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.haiphamcoder.cdp.infrastructure.security.oauth2.CustomOAuth2UserService;
import com.haiphamcoder.cdp.infrastructure.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.haiphamcoder.cdp.infrastructure.security.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.haiphamcoder.cdp.infrastructure.security.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.haiphamcoder.cdp.shared.UnauthorizedAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
@Slf4j
public class SecurityConfiguration {
        private static final String[] AUTH_WHITELIST = {
                        "/",
                        "/api/v1/auth/register",
                        "/api/v1/auth/authenticate",
                        "/api/v1/oauth2/**",
                        "/api/v1/oauth2/authorize/**",
                        "/api/v1/oauth2/callback/**",
                        "/api/docs/**",
                        "/swagger-ui/**",
        };

        private static final String ADMIN_ENDPOINT = "/api/v1/admin/**";

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final UnauthorizedAuthenticationEntryPoint unauthorizedAuthenticationEntryPoint;
        private final LogoutHandler logoutHandler;
        private final CustomOAuth2UserService customOAuth2UserService;
        private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
        private final OAuth2AuthenticationFailureHandler oAuth2AuthorizationFailureHandler;
        private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.disable())
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
                                .oauth2Login(login -> login
                                                .authorizationEndpoint(authorization -> authorization
                                                                .baseUri("/api/v1/oauth2/authorize")
                                                                .authorizationRequestRepository(
                                                                                authorizationRequestRepository))
                                                .redirectionEndpoint(redirection -> redirection
                                                                .baseUri("/api/v1/oauth2/callback"))
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userService(customOAuth2UserService))
                                                .successHandler(oAuth2AuthenticationSuccessHandler)
                                                .failureHandler(oAuth2AuthorizationFailureHandler))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordBodyAuthenticationFilter.class)
                                .logout(logout -> logout.logoutUrl("/api/v1/auth/logout")
                                                .addLogoutHandler(logoutHandler)
                                                .logoutSuccessHandler((request, response,
                                                                authentication) -> SecurityContextHolder
                                                                                .clearContext()));
                return http.build();
        }
}
