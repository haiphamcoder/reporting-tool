package com.haiphamcoder.cdp.application.service;

import com.haiphamcoder.cdp.domain.entity.RefreshToken;

public interface RefreshTokenService {
    public RefreshToken getTokenByValue(String tokenValue);

    public RefreshToken saveUserToken(RefreshToken token);

    public void deleteToken(Long tokenId);
}
