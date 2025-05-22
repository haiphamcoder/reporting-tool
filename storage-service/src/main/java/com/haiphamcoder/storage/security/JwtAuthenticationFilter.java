package com.haiphamcoder.storage.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.haiphamcoder.storage.config.CommonConstants;
import com.haiphamcoder.storage.shared.CookieUtils;

import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final Optional<String> accessTokenInCookie = CookieUtils
                .getCookie(request, CommonConstants.ACCESS_TOKEN_COOKIE_NAME).map(Cookie::getValue);

        if (!accessTokenInCookie.isPresent()) {
            filterChain.doFilter(request, response);
            return;
        }

        final String accessToken = accessTokenInCookie.get();
        try {
            final String username = jwtTokenProvider.extractUsername(accessToken);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                log.info("Start validating token");
                if (!jwtTokenProvider.isTokenValid(accessToken)) {
                    filterChain.doFilter(request, response);
                    return;
                }
                log.info("Username: {}", username);
                log.info("Token: {}", accessToken);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            try {
                response.getWriter().write("{\"message\": \"Invalid token signature\"}");
            } catch (IOException ex) {
                log.error("Error writing response: {}", ex.getMessage());
            }
            return;
        }

        filterChain.doFilter(request, response);
    }

}
