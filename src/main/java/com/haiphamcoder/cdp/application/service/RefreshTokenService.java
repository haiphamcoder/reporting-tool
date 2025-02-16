package com.haiphamcoder.cdp.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.domain.entity.RefreshToken;
import com.haiphamcoder.cdp.domain.repository.RefreshTokenRepository;
import com.haiphamcoder.cdp.shared.HashUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public List<RefreshToken> getAllValidTokens(Long userId) {
        return refreshTokenRepository.getAllTokensByUserId(userId);
    }

    public RefreshToken getValidToken(Long userId, String tokenValue) {
        String hashedTokenValue = HashUtils.hashSHA256(tokenValue);
        return refreshTokenRepository.getValidTokenByUserIdAndTokenValue(userId, hashedTokenValue)
                .orElse(null);
    }

    public RefreshToken saveUserToken(RefreshToken token) {
        RefreshToken clonedToken = token.clone();
        clonedToken.setTokenValue(HashUtils.hashSHA256(token.getTokenValue()));
        return refreshTokenRepository.saveToken(clonedToken);
    }

    public void saveAllUserTokens(List<RefreshToken> tokens) {
        List<RefreshToken> clonedTokens = tokens.stream()
                .map(RefreshToken::clone)
                .map(token -> {
                    token.setTokenValue(HashUtils.hashSHA256(token.getTokenValue()));
                    return token;
                })
                .collect(Collectors.toList());
        refreshTokenRepository.saveAllTokens(clonedTokens);
    }

    public void deleteToken(Long tokenId) {
        refreshTokenRepository.deleteTokenById(tokenId);
    }
}
