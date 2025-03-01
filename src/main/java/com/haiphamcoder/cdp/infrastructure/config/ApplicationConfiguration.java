package com.haiphamcoder.cdp.infrastructure.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.haiphamcoder.cdp.infrastructure.security.CustomUserDetailsService;
import com.haiphamcoder.cdp.infrastructure.security.auditing.ApplicationAuditAware;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationConfiguration {

    private final CustomUserDetailsService customUserDetailsService;

    @Value("${cdp.cors.max-age-seconds:3600}")
    private Long corsMaxAgeInSec;

    
    @Bean(name = "passwordEncoder")
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = "auditorAware")
    AuditorAware<Long> auditorAware() {
        return new ApplicationAuditAware();
    }

    @Bean
    AuthenticationManager authenticationManager(@Qualifier("passwordEncoder") PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        ProviderManager providerManager = new ProviderManager(Arrays.asList(provider));
        return providerManager;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration apiCorsConfiguration = new CorsConfiguration();
        apiCorsConfiguration.setAllowedOriginPatterns(Arrays.asList("*"));
        apiCorsConfiguration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE"));
        apiCorsConfiguration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        apiCorsConfiguration.setExposedHeaders(Arrays.asList("Authorization"));
        apiCorsConfiguration.setMaxAge(corsMaxAgeInSec);
        apiCorsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/v1/**", apiCorsConfiguration);
        return source;
    }

    @Bean
    HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        firewall.setAllowSemicolon(true);
        firewall.setAllowUrlEncodedDoubleSlash(true);
        firewall.setAllowUrlEncodedPercent(true);
        return firewall;
    }

}
