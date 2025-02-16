package com.haiphamcoder.cdp.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.domain.entity.AccessToken;
import com.haiphamcoder.cdp.domain.repository.AccessTokenRepository;
import com.haiphamcoder.cdp.shared.HashUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccessTokenService {
    private final AccessTokenRepository accessTokenRepository;

    public AccessToken getValidToken(Long refreshTokenId, String tokenValue) {
        String hashedTokenValue = HashUtils.hashSHA256(tokenValue);
        return accessTokenRepository.getValidTokenByRefreshTokenIdAndTokenValue(refreshTokenId, hashedTokenValue)
                .orElse(null);
    }

    public AccessToken saveUserToken(AccessToken token) {
        AccessToken clonedToken = token.clone();
        clonedToken.setTokenValue(HashUtils.hashSHA256(token.getTokenValue()));
        return accessTokenRepository.saveToken(clonedToken);
    }

    public void saveAllUserTokens(List<AccessToken> tokens) {
        List<AccessToken> clonedTokens = tokens.stream()
                .map(AccessToken::clone)
                .map(token -> {
                    token.setTokenValue(HashUtils.hashSHA256(token.getTokenValue()));
                    return token;
                })
                .collect(Collectors.toList());
        accessTokenRepository.saveAllTokens(clonedTokens);
    }
}
