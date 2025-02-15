package com.haiphamcoder.cdp.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.domain.entity.Token;
import com.haiphamcoder.cdp.domain.repository.TokenRepository;
import com.haiphamcoder.cdp.shared.HashUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public List<Token> getAllValidTokens(Long userId) {
        return tokenRepository.getAllValidTokensByUserId(userId);
    }

    public Token getValidToken(Long userId, String tokenValue) {
        String hashedTokenValue = HashUtils.hashSHA256(tokenValue);
        return tokenRepository.getValidTokenByUserIdAndTokenValue(userId, hashedTokenValue)
                .orElse(null);
    }

    public Token saveUserToken(Token token) {
        Token clonedToken = token.clone();
        clonedToken.setTokenValue(HashUtils.hashSHA256(token.getTokenValue()));
        return tokenRepository.saveToken(clonedToken);
    }

    public void saveAllUserTokens(List<Token> tokens) {
        List<Token> clonedTokens = tokens.stream()
                .map(Token::clone)
                .map(token -> {
                    token.setTokenValue(HashUtils.hashSHA256(token.getTokenValue()));
                    return token;
                })
                .collect(Collectors.toList());
        tokenRepository.saveAllTokens(clonedTokens);
    }
}
