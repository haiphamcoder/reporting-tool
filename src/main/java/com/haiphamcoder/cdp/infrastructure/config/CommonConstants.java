package com.haiphamcoder.cdp.infrastructure.config;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonConstants {
    public static final int AUTHEN_FAILED = 0;
    public static final int AUTHEN_SUCCESS = 1;

    public static final int CONNECTOR_TYPE_CSV = 1;

    // Authentication and Authorization Cookies
    public static final String USER_ID_COOKIE_NAME = "user-id";
    public static final String ACCESS_TOKEN_COOKIE_NAME = "access-token";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh-token";

    // OAuth2 Cookie Constants
    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect-uri";
    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    public static final int COOKIE_EXPIRE_SECONDS = 180;
    
}
