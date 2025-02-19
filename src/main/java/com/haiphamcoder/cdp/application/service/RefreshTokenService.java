package com.haiphamcoder.cdp.application.service;

import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.domain.entity.RefreshToken;
import com.haiphamcoder.cdp.domain.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken getTokenByValue(String tokenValue) {
        return refreshTokenRepository.getTokenByTokenValue(tokenValue).orElse(null);
    }

    public RefreshToken saveUserToken(RefreshToken token) {
        return refreshTokenRepository.saveToken(token).orElse(null);
    }

    public void deleteToken(Long tokenId) {
        refreshTokenRepository.deleteTokenById(tokenId);
    }
}
