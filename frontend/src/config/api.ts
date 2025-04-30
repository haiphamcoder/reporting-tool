
export const API_CONFIG = {
    BASE_URL: 'http://reporting-tool.site:8080',
    ENDPOINTS: {
        AUTHENTICATE: '/api/v1/auth/authenticate',
        REDIRECT_LOGIN_GOOGLE: '/oauth2/authorization/google?redirect-uri=http://reporting-tool.site/dashboard',
        AUTH_INFO: '/api/v1/auth/me',
        LOGOUT: '/api/v1/auth/logout',
        USER: '/api/v1/user',
    }
} as const;
