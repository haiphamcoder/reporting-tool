package com.haiphamcoder.cdp.application.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.domain.entity.RefreshToken;
import com.haiphamcoder.cdp.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public List<RefreshToken> getAllValidTokens(Long userId) {
        return refreshTokenRepository.getAllTokensByUserId(userId);
    }

    public RefreshToken getValidToken(Long userId, String tokenValue) {
        String tokenValueInBase64 = Base64.getEncoder().encodeToString(tokenValue.getBytes(StandardCharsets.UTF_8));
        return refreshTokenRepository.getValidTokenByUserIdAndTokenValue(userId, tokenValueInBase64)
                .orElse(null);
    }

    public RefreshToken getTokenByValue(String tokenValue) {
        Optional<RefreshToken> token = refreshTokenRepository
                .getTokenByTokenValue(Base64.getEncoder().encodeToString(tokenValue.getBytes(StandardCharsets.UTF_8)));
        if (token.isPresent()) {
            token.get().setTokenValue(
                    new String(Base64.getDecoder().decode(token.get().getTokenValue()), StandardCharsets.UTF_8));
            return token.get();
        }
        return null;
    }

    public RefreshToken saveUserToken(RefreshToken token) {
        RefreshToken clonedToken = new RefreshToken(token);
        clonedToken.setTokenValue(
                Base64.getEncoder().encodeToString(clonedToken.getTokenValue().getBytes(StandardCharsets.UTF_8)));
        RefreshToken savedToken = refreshTokenRepository.saveToken(clonedToken).orElse(null);
        if (savedToken != null) {
            savedToken.setTokenValue(
                    new String(Base64.getDecoder().decode(savedToken.getTokenValue()), StandardCharsets.UTF_8));
        }
        return savedToken;
    }

    public void saveAllUserTokens(List<RefreshToken> tokens) {
        List<RefreshToken> clonedTokens = tokens.stream()
                .map(token -> {
                    RefreshToken clonedToken = new RefreshToken(token);
                    clonedToken.setTokenValue(Base64.getEncoder()
                            .encodeToString(clonedToken.getTokenValue().getBytes(StandardCharsets.UTF_8)));
                    return clonedToken;
                }).toList();
        refreshTokenRepository.saveAllTokens(clonedTokens);
    }

    public void deleteToken(Long tokenId) {
        refreshTokenRepository.deleteTokenById(tokenId);
    }
}
