package com.haiphamcoder.reporting.application.service.impl;

import org.springframework.stereotype.Service;

import com.haiphamcoder.reporting.application.service.RefreshTokenService;
import com.haiphamcoder.reporting.domain.entity.RefreshToken;
import com.haiphamcoder.reporting.domain.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken getTokenByValue(String tokenValue) {
        return refreshTokenRepository.getTokenByTokenValue(tokenValue).orElse(null);
    }

    @Override
    public RefreshToken saveUserToken(RefreshToken token) {
        return refreshTokenRepository.saveToken(token).orElse(null);
    }

    @Override
    public void deleteToken(Long tokenId) {
        refreshTokenRepository.deleteTokenById(tokenId);
    }
}
