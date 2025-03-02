package com.haiphamcoder.cdp.infrastructure.security.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OAuth2Provider {
    LOCAL("local"),
    GOOGLE("google");
    
    @Getter
    private final String value;
}
