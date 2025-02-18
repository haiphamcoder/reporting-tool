package com.haiphamcoder.cdp.application.service;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haiphamcoder.cdp.domain.entity.AccessToken;
import com.haiphamcoder.cdp.domain.entity.RefreshToken;
import com.haiphamcoder.cdp.shared.ApiResponse;
import com.haiphamcoder.cdp.shared.ApiResponseFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {

    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            ApiResponse<String> apiResponse = ApiResponseFactory
                    .createUnauthorizedResponse("You are not authorized to access this resource");
            try {
                new ObjectMapper().writeValue(response.getOutputStream(), apiResponse);
            } catch (Exception e) {
                log.error("Error writing response: {}", e.getMessage());
            }
            return;
        }
        final String accessToken = authHeader.substring(7);
        AccessToken storedAccessToken = accessTokenService.getTokenByValue(accessToken);
        if (storedAccessToken == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            ApiResponse<String> apiResponse = ApiResponseFactory
                    .createUnauthorizedResponse("You are not authorized to access this resource");
            try {
                new ObjectMapper().writeValue(response.getOutputStream(), apiResponse);
            } catch (Exception e) {
                log.error("Error writing response: {}", e.getMessage());
            }
            return;
        }
        RefreshToken storedRefreshToken = storedAccessToken.getRefreshToken();
        refreshTokenService.deleteToken(storedRefreshToken.getId());
        SecurityContextHolder.clearContext();
    }

}
