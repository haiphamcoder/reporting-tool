package com.haiphamcoder.cdp.infrastructure.security.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OAuth2AuthorizationRequestParams {

    SUCCESS_REDIRECT_URI("success-redirect-uri"),
    FAILURE_REDIRECT_URI("failure-redirect-uri");

    @Getter
    private final String value;

}
