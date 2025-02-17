package com.haiphamcoder.cdp.application.service;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.domain.entity.AccessToken;
import com.haiphamcoder.cdp.domain.entity.RefreshToken;
import com.haiphamcoder.cdp.domain.repository.AccessTokenRepository;
import com.haiphamcoder.cdp.domain.repository.RefreshTokenRepository;
import com.haiphamcoder.cdp.shared.HashUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {

    private final AccessTokenRepository accessTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        log.info("Logging out user with auth header: {}", authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        final String accessToken = authHeader.substring(7);
        Optional<AccessToken> storedAccessToken = accessTokenRepository.getTokenByTokenValue(HashUtils.hashSHA256(accessToken));
        if (storedAccessToken.isEmpty()) {
            return;
        }
        RefreshToken storedRefreshToken = storedAccessToken.get().getRefreshToken();
        refreshTokenRepository.deleteTokenById(storedRefreshToken.getId());
        SecurityContextHolder.clearContext();
    }

}
