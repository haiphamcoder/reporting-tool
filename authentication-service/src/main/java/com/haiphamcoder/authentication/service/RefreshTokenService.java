package com.haiphamcoder.authentication.service;

import com.haiphamcoder.authentication.domain.entity.RefreshToken;

public interface RefreshTokenService {
    public RefreshToken getTokenByValue(String tokenValue);

    public RefreshToken saveUserToken(RefreshToken token);

    public void deleteToken(Long tokenId);
}
