package com.haiphamcoder.cdp.domain.repository;

import java.util.List;
import java.util.Optional;

import com.haiphamcoder.cdp.domain.entity.AccessToken;

public interface AccessTokenRepository {
    Optional<AccessToken> getTokenByTokenValue(String tokenValue);

    Optional<AccessToken> getValidTokenByRefreshTokenIdAndTokenValue(Long refreshTokenId, String tokenValue);

    Optional<AccessToken> saveToken(AccessToken token);

    void saveAllTokens(List<AccessToken> tokens);

    void deleteTokenById(Long tokenId);
}