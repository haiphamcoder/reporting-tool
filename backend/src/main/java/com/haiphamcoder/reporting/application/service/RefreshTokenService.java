package com.haiphamcoder.reporting.application.service;

import com.haiphamcoder.reporting.domain.entity.RefreshToken;

public interface RefreshTokenService {
    public RefreshToken getTokenByValue(String tokenValue);

    public RefreshToken saveUserToken(RefreshToken token);

    public void deleteToken(Long tokenId);
}
