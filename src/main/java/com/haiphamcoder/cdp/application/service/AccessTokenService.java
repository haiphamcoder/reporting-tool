package com.haiphamcoder.cdp.application.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.domain.entity.AccessToken;
import com.haiphamcoder.cdp.domain.repository.AccessTokenRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccessTokenService {
    private final AccessTokenRepository accessTokenRepository;

    public AccessToken getValidToken(Long refreshTokenId, String tokenValue) {
        String tokenValueInBase64 = Base64.getEncoder().encodeToString(tokenValue.getBytes(StandardCharsets.UTF_8));
        return accessTokenRepository.getValidTokenByRefreshTokenIdAndTokenValue(refreshTokenId, tokenValueInBase64)
                .orElse(null);
    }

    public Optional<AccessToken> getTokenByValue(String tokenValue) {
        Optional<AccessToken> token = accessTokenRepository.getTokenByTokenValue(Base64.getEncoder().encodeToString(tokenValue.getBytes(StandardCharsets.UTF_8)));
        if (token.isPresent()) {
            token.get().setTokenValue(new String(Base64.getDecoder().decode(token.get().getTokenValue()), StandardCharsets.UTF_8));
            return token;
        }
        return null;
    }

    public AccessToken saveUserToken(AccessToken token) {
        AccessToken clonedToken = token.clone();
        clonedToken.setTokenValue(Base64.getEncoder().encodeToString(clonedToken.getTokenValue().getBytes(StandardCharsets.UTF_8)));
        return accessTokenRepository.saveToken(clonedToken);
    }

    public void saveAllUserTokens(List<AccessToken> tokens) {
        List<AccessToken> clonedTokens = tokens.stream()
                .map(AccessToken::clone)
                .map(token -> {
                    token.setTokenValue(Base64.getEncoder().encodeToString(token.getTokenValue().getBytes(StandardCharsets.UTF_8)));
                    return token;
                })
                .collect(Collectors.toList());
        accessTokenRepository.saveAllTokens(clonedTokens);
    }
}
