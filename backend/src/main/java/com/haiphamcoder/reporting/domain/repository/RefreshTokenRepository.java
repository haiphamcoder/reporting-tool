package com.haiphamcoder.reporting.domain.repository;

import java.util.List;
import java.util.Optional;

import com.haiphamcoder.reporting.domain.entity.RefreshToken;

public interface RefreshTokenRepository {

    List<RefreshToken> getAllTokensByUserId(Long userId);
    
    Optional<RefreshToken> getTokenByTokenValue(String tokenValue);

    Optional<RefreshToken> getValidTokenByUserIdAndTokenValue(Long userId, String tokenValue);

    Optional<RefreshToken> saveToken(RefreshToken token);

    void saveAllTokens(List<RefreshToken> tokens);

    void deleteTokenById(Long tokenId);
}
