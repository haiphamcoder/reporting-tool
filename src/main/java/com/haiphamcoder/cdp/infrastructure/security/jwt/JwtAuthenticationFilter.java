package com.haiphamcoder.cdp.infrastructure.security.jwt;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.haiphamcoder.cdp.domain.entity.User;
import com.haiphamcoder.cdp.infrastructure.config.SecurityConfiguration;
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
        
        String requestURI = request.getRequestURI();
        if (Arrays.stream(SecurityConfiguration.AUTH_WHITELIST).anyMatch(uri -> requestURI.startsWith(uri))) {
            filterChain.doFilter(request, response);
            return;
        }

        final Optional<String> userIdInCookie = CookieUtils
                .getCookie(request, USER_ID_HEADER).map(Cookie::getValue);
        final Optional<String> accessTokenInCookie = CookieUtils
                .getCookie(request, OAuth2AuthorizationRequestParams.ACCESS_TOKEN.getValue()).map(Cookie::getValue);
        final Optional<String> refreshTokenInCookie = CookieUtils
                .getCookie(request, OAuth2AuthorizationRequestParams.REFRESH_TOKEN.getValue()).map(Cookie::getValue);

        if (!userIdInCookie.isPresent() || !accessTokenInCookie.isPresent() || !refreshTokenInCookie.isPresent()) {
            filterChain.doFilter(request, response);
            return;
        }

        final String userId = userIdInCookie.get();
        final String accessToken = accessTokenInCookie.get();
        final String refreshToken = refreshTokenInCookie.get();

        final String username = jwtTokenProvider.extractUsername(accessToken);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (((User) userDetails).getId() == Long.parseLong(userId)) {
                if (!jwtTokenProvider.isTokenValid(accessToken, userDetails)){
                    if (!jwtTokenProvider.isTokenValid(refreshToken, userDetails)) {
                        filterChain.doFilter(request, response);
                        return;
                    } else {
                        String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
                        CookieUtils.addCookie(response, OAuth2AuthorizationRequestParams.ACCESS_TOKEN.getValue(), newAccessToken);
                    }
                }
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

}
