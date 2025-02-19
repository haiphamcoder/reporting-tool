package com.haiphamcoder.cdp.application.service;

import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.domain.entity.AccessToken;
import com.haiphamcoder.cdp.domain.repository.AccessTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccessTokenService {
    private final AccessTokenRepository accessTokenRepository;

    public AccessToken getTokenByValue(String tokenValue) {
        return accessTokenRepository
                .getTokenByTokenValue(tokenValue)
                .orElse(null);
    }

    public AccessToken saveUserToken(AccessToken token) {
        return accessTokenRepository.saveToken(token).orElse(null);
    }

    public void deleteTokenById(Long tokenId) {
        accessTokenRepository.deleteTokenById(tokenId);
    }
}
