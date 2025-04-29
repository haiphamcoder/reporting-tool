package com.haiphamcoder.cdp.infrastructure.security.oauth2;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OAuth2Provider {
    LOCAL("local"),
    GOOGLE("google");

    @Getter
    private final String name;

    public static OAuth2Provider fromName(String name) {
        return Arrays.stream(OAuth2Provider.values())
                .filter(provider -> provider.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid OAuth2 provider: " + name));
    }
}
