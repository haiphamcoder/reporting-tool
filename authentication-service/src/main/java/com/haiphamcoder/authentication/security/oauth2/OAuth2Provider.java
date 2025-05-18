package com.haiphamcoder.authentication.security.oauth2;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OAuth2Provider {
    LOCAL("local"),
    GOOGLE("google"),
    FACEBOOK("facebook"),
    GITHUB("github");

    @Getter
    private final String registrationId;

    public static OAuth2Provider fromRegistrationId(String registrationId) {
        return Arrays.stream(OAuth2Provider.values())
                .filter(provider -> provider.getRegistrationId().equals(registrationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid OAuth2 provider: " + registrationId));
    }
}
