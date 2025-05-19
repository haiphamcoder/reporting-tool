package com.haiphamcoder.reporting.config;

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

    // Source Permission
    public static final String SOURCE_PERMISSION_NONE = "---";
    public static final String SOURCE_PERMISSION_READ = "r--";
    public static final String SOURCE_PERMISSION_WRITE = "-w-";
    public static final String SOURCE_PERMISSION_READ_WRITE = "rw-";
    public static final String SOURCE_PERMISSION_EXECUTE = "--x";
    public static final String SOURCE_PERMISSION_READ_EXECUTE = "r-x";
    public static final String SOURCE_PERMISSION_WRITE_EXECUTE = "-wx";
    public static final String SOURCE_PERMISSION_ALL = "rwx";
    
}
