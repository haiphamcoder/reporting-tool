package com.haiphamcoder.reporting.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.haiphamcoder.reporting.config.CommonConstants;
import com.haiphamcoder.reporting.config.SecurityConfiguration;
import com.haiphamcoder.reporting.shared.CookieUtils;

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

        String requestURI = request.getRequestURI();
        log.info("Request URI: {}", requestURI);
        for (String uri : SecurityConfiguration.AUTH_WHITELIST) {
            if (requestURI.equals("/") || (!uri.equals("/") && requestURI.startsWith(uri.replace("*", "")))) {
                log.info("Request URI in whitelist: {}", uri);
                filterChain.doFilter(request, response);
                return;
            }
        }

        log.info("Request URI: {}", requestURI);

        final Optional<String> userIdInCookie = CookieUtils
                .getCookie(request, CommonConstants.USER_ID_COOKIE_NAME).map(Cookie::getValue);
        final Optional<String> accessTokenInCookie = CookieUtils
                .getCookie(request, CommonConstants.ACCESS_TOKEN_COOKIE_NAME).map(Cookie::getValue);
        final Optional<String> refreshTokenInCookie = CookieUtils
                .getCookie(request, CommonConstants.REFRESH_TOKEN_COOKIE_NAME).map(Cookie::getValue);

        if (!userIdInCookie.isPresent() || !accessTokenInCookie.isPresent() || !refreshTokenInCookie.isPresent()) {
            filterChain.doFilter(request, response);
            return;
        }

        final String userId = userIdInCookie.get();
        final String accessToken = accessTokenInCookie.get();
        final String refreshToken = refreshTokenInCookie.get();

        try {
            final String username = jwtTokenProvider.extractUsername(accessToken);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                log.info("User Details: {}", userDetails);
                if (((User) userDetails).getId() == Long.parseLong(userId)) {
                    if (!jwtTokenProvider.isTokenValid(accessToken, userDetails)) {
                        if (!jwtTokenProvider.isTokenValid(refreshToken, userDetails)) {
                            filterChain.doFilter(request, response);
                            return;
                        } else {
                            String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
                            CookieUtils.addCookie(response, OAuth2AuthorizationRequestParams.ACCESS_TOKEN.getValue(),
                                    newAccessToken);
                        }
                    }
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
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
