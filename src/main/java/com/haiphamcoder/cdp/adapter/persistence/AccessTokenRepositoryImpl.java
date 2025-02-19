package com.haiphamcoder.cdp.adapter.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.cdp.domain.entity.AccessToken;
import com.haiphamcoder.cdp.domain.repository.AccessTokenRepository;

import lombok.RequiredArgsConstructor;

@Repository
interface AccessTokenJpaRepository extends JpaRepository<AccessToken, Long> {
    Optional<AccessToken> findTokenByTokenValue(String tokenValue);
}

@Component
@RequiredArgsConstructor
public class AccessTokenRepositoryImpl implements AccessTokenRepository {
    private final AccessTokenJpaRepository accessTokenJpaRepository;

    @Override
    public Optional<AccessToken> getTokenByTokenValue(String tokenValue) {
        return accessTokenJpaRepository.findTokenByTokenValue(tokenValue);
    }

    @Override
    public Optional<AccessToken> getValidTokenByRefreshTokenIdAndTokenValue(Long refreshTokenId, String tokenValue) {
        LocalDateTime now = LocalDateTime.now();
        return accessTokenJpaRepository.findTokenByTokenValue(tokenValue)
                .filter(token -> token.getRefreshToken().getId().equals(refreshTokenId)
                        && token.getExpiredAt().isAfter(now));
    }

    @Override
    public Optional<AccessToken> saveToken(AccessToken token) {
        return Optional.of(accessTokenJpaRepository.save(token));
    }

    @Override
    public void saveAllTokens(List<AccessToken> tokens) {
        accessTokenJpaRepository.saveAll(tokens);
    }

    @Override
    public void deleteTokenById(Long tokenId) {
        accessTokenJpaRepository.deleteById(tokenId);
    }
}
