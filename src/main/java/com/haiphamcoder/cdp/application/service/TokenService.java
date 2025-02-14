package com.haiphamcoder.cdp.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.domain.entity.Token;
import com.haiphamcoder.cdp.domain.repository.TokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public List<Token> getAllValidTokens(Long userId){
        return tokenRepository.getAllValidTokensByUserId(userId);
    }

    public Token getValidToken(Long userId, String tokenValue) {
        return tokenRepository.getValidTokenByUserIdAndTokenValue(userId, tokenValue)
                .orElse(null);
    }

    public Token saveUserToken(Token token) {
        return tokenRepository.saveToken(token);
    }

    public void saveAllUserTokens(List<Token> tokens) {
        tokenRepository.saveAllTokens(tokens);
    }
}
