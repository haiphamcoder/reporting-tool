
export const API_CONFIG = {
    BASE_URL: 'http://reporting-tool-backend:8080',
    OAUTH2_REDIRECT_URI: 'http://reporting-tool-backend:8080/oauth2/redirect',
    ENDPOINTS: {
        AUTHENTICATE: '/api/v1/auth/authenticate',
        REDIRECT_LOGIN_GOOGLE: '/oauth2/authorization/google',
        AUTH_INFO: '/api/v1/auth/me',
        LOGOUT: '/api/v1/auth/logout',
        USER: '/api/v1/user',
    }
} as const;
