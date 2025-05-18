package com.haiphamcoder.gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import com.haiphamcoder.gateway.shared.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements GatewayFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String accessToken = getAccessTokenFromCookie(request);
        log.info("Access token: {}", accessToken);

        if (!StringUtils.isNullOrEmpty(accessToken)) {
            try {
                if (jwtTokenProvider.isTokenValid(accessToken)) {
                    ServerHttpRequest mutatedRequest = request.mutate()
                        .build();
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                }
            } catch (SignatureException e) {
                log.error("Invalid JWT token: {}", e.getMessage());
                return onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
            }
        }

        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }

    private String getAccessTokenFromCookie(ServerHttpRequest request) {
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        if (cookies == null || cookies.isEmpty()) {
            return null;
        }

        HttpCookie accessTokenCookie = cookies.getFirst("access-token");
        return accessTokenCookie != null ? accessTokenCookie.getValue() : null;
    }

}
