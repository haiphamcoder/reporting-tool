package com.haiphamcoder.cdp.infrastructure.security.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OAuth2AuthorizationRequestParams {

    REDIRECT_URI("redirect-uri"),
    ACCESS_TOKEN("access-token"),
    REFRESH_TOKEN("refresh-token");

    @Getter
    private final String value;

}
