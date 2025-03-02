package com.haiphamcoder.cdp.infrastructure.security.jwt;

import java.io.IOException;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.haiphamcoder.cdp.domain.entity.User;
import com.haiphamcoder.cdp.infrastructure.security.CustomUserDetailsService;
import com.haiphamcoder.cdp.infrastructure.security.oauth2.OAuth2AuthorizationRequestParams;
import com.haiphamcoder.cdp.shared.CookieUtils;
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
    private static final String USER_ID_HEADER = "user-id";

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final Optional<String> userIdInCookie = CookieUtils
                .getCookie(request, USER_ID_HEADER).map(Cookie::getValue);
        log.info("User ID in cookie: {}", userIdInCookie);
        final Optional<String> accessTokenInCookie = CookieUtils
                .getCookie(request, OAuth2AuthorizationRequestParams.ACCESS_TOKEN.getValue()).map(Cookie::getValue);
        log.info("Access token in cookie: {}", accessTokenInCookie);
        final Optional<String> refreshTokenInCookie = CookieUtils
                .getCookie(request, OAuth2AuthorizationRequestParams.REFRESH_TOKEN.getValue()).map(Cookie::getValue);
        log.info("Refresh token in cookie: {}", refreshTokenInCookie);

        if (!userIdInCookie.isPresent() || !accessTokenInCookie.isPresent() || !refreshTokenInCookie.isPresent()) {
            filterChain.doFilter(request, response);
            return;
        }

        final String userId = userIdInCookie.get();
        final String accessToken = accessTokenInCookie.get();

        log.info("User ID: {}", userId);
        log.info("Access token: {}", accessToken);

        final String username = jwtTokenProvider.extractUsername(accessToken);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtTokenProvider.isTokenValid(accessToken, userDetails) && ((User) userDetails).getId() == Long.parseLong(userId)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                        null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

}
