
export const API_CONFIG = {
    BASE_URL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8765',
    ENDPOINTS: {
        AUTHENTICATE: '/authentication/authenticate',
        REGISTER: '/authentication/register',
        GET_CURRENT_USER: '/authentication/me',
        UPDATE_USER: '/user-management/:user_id',
        REDIRECT_LOGIN_GOOGLE: '/authentication/oauth2/authorization/google?redirect-uri=' + (import.meta.env.VITE_FRONTEND_URL || 'http://localhost:5173') + '/dashboard',
        AUTH_INFO: '/api/v1/auth/me',
        LOGOUT: '/authentication/logout',
        USER: '/api/v1/user',
        STATISTICS: '/reporting/statistics',
        CONNECTOR: '/api/v1/connector',
        SOURCES: '/reporting/sources',
        INIT_SOURCES: '/reporting/sources/init',
        SOURCE_UPLOAD_FILE: '/reporting/sources/upload-file',
        SOURCE_CONFIRM_SCHEMA: '/reporting/sources/confirm-schema',
        SOURCE_SUBMIT_IMPORT: '/data-processing/sources/import',
        SOURCE_PREVIEW: '/data-processing/sources/:source_id/preview',
        SOURCE_GET_SCHEMA: '/data-processing/sources/schema',
        SOURCE_DETAILS: '/reporting/sources/:id',
        USER_MANAGEMENT: '/user-management',
    }
} as const;
