
export const API_CONFIG = {
    BASE_URL: 'http://localhost:8765',
    ENDPOINTS: {
        AUTHENTICATE: '/authentication/authenticate',
        REGISTER: '/authentication/register',
        GET_CURRENT_USER: '/authentication/me',
        REDIRECT_LOGIN_GOOGLE: '/oauth2/authorization/google?redirect-uri=http://localhost:5173/dashboard',
        AUTH_INFO: '/api/v1/auth/me',
        LOGOUT: '/authentication/logout',
        USER: '/api/v1/user',
        STATISTICS: '/api/v1/statistics',
        CONNECTOR: '/api/v1/connector',
        SOURCES: '/reporting/sources',
        SOURCE_DETAILS: '/reporting/sources/:id',
    }
} as const;
