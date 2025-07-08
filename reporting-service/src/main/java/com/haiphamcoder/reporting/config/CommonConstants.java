package com.haiphamcoder.reporting.config;

import java.util.Map;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonConstants {
    public static final int AUTHEN_FAILED = 0;
    public static final int AUTHEN_SUCCESS = 1;

    public static final int CONNECTOR_TYPE_CSV = 1;
    public static final int CONNECTOR_TYPE_EXCEL = 2;
    
    // Authentication and Authorization Cookies
    public static final String USER_ID_COOKIE_NAME = "user-id";
    public static final String ACCESS_TOKEN_COOKIE_NAME = "access-token";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh-token";

    // OAuth2 Cookie Constants
    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect-uri";
    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    public static final int COOKIE_EXPIRE_SECONDS = 180;

    // Source Status
    public static final int SOURCE_STATUS_INIT = 1;
    public static final int SOURCE_STATUS_PREPARED = 2;
    public static final int SOURCE_STATUS_PROCESSING = 3;
    public static final int SOURCE_STATUS_READY = 4;
    public static final int SOURCE_STATUS_FAILED = -1;

    public static final String SOURCE_STATUS_INIT_STRING = "init";
    public static final String SOURCE_STATUS_PREPARED_STRING = "prepared";
    public static final String SOURCE_STATUS_PROCESSING_STRING = "processing";
    public static final String SOURCE_STATUS_READY_STRING = "ready";
    public static final String SOURCE_STATUS_FAILED_STRING = "failed";

    public static final Map<Integer, String> SOURCE_STATUS_MAP = Map.of(
        SOURCE_STATUS_INIT, SOURCE_STATUS_INIT_STRING,
        SOURCE_STATUS_PREPARED, SOURCE_STATUS_PREPARED_STRING,
        SOURCE_STATUS_PROCESSING, SOURCE_STATUS_PROCESSING_STRING,
        SOURCE_STATUS_READY, SOURCE_STATUS_READY_STRING,
        SOURCE_STATUS_FAILED, SOURCE_STATUS_FAILED_STRING
    );
    
}
