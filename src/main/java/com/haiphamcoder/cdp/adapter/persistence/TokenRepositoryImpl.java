package com.haiphamcoder.cdp.adapter.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.cdp.domain.entity.Token;
import com.haiphamcoder.cdp.domain.repository.TokenRepository;

import lombok.RequiredArgsConstructor;

@Repository
interface TokenJpaRepository extends JpaRepository<Token, Long> {
    List<Token> findAllByUserIdAndExpiredFalseAndRevokedFalse(Long userId);

    Optional<Token> findTokenByTokenValue(String tokenValue);

}

@Component
@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepository {
    private final TokenJpaRepository tokenJpaRepository;

    @Override
    public List<Token> getAllValidTokensByUserId(Long userId) {
        return tokenJpaRepository.findAllByUserIdAndExpiredFalseAndRevokedFalse(userId);
    }

    @Override
    public Optional<Token> getTokenByTokenValue(String tokenValue) {
        return tokenJpaRepository.findTokenByTokenValue(tokenValue);
    }

    @Override
    public Optional<Token> getValidTokenByUserIdAndTokenValue(Long userId, String tokenValue) {
        return tokenJpaRepository.findTokenByTokenValue(tokenValue)
                .filter(token -> token.getUser().getId().equals(userId) && !token.isExpired() && !token.isRevoked());
    }

    @Override
    public Token saveToken(Token token) {
        return tokenJpaRepository.save(token);
    }

    @Override
    public void saveAllTokens(List<Token> tokens) {
        tokenJpaRepository.saveAll(tokens);
    }
}
