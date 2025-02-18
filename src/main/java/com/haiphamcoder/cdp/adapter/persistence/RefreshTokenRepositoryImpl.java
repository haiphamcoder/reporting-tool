package com.haiphamcoder.cdp.adapter.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.cdp.domain.entity.RefreshToken;
import com.haiphamcoder.cdp.domain.repository.RefreshTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {
    List<RefreshToken> findAllByUserId(Long userId);

    Optional<RefreshToken> findTokenByTokenValue(String tokenValue);

}

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Override
    public List<RefreshToken> getAllTokensByUserId(Long userId) {
        return refreshTokenJpaRepository.findAllByUserId(userId);
    }

    @Transactional
    @Override
    public Optional<RefreshToken> getTokenByTokenValue(String tokenValue) {
        return refreshTokenJpaRepository.findTokenByTokenValue(tokenValue);
    }

    @Override
    public Optional<RefreshToken> getValidTokenByUserIdAndTokenValue(Long userId, String tokenValue) {
        LocalDateTime now = LocalDateTime.now();
        return refreshTokenJpaRepository.findTokenByTokenValue(tokenValue)
                .filter(token -> token.getUser().getId().equals(userId) && token.getExpiredAt().isAfter(now));
    }

    @Override
    public Optional<RefreshToken> saveToken(RefreshToken token) {
        return Optional.of(refreshTokenJpaRepository.save(token));
    }

    @Override
    public void saveAllTokens(List<RefreshToken> tokens) {
        refreshTokenJpaRepository.saveAll(tokens);
    }

    @Override
    public void deleteTokenById(Long tokenId) {
        refreshTokenJpaRepository.deleteById(tokenId);
    }
}
