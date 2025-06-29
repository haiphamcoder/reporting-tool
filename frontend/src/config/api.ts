import { config } from '../../config/env.js';

export const API_CONFIG = {
    BASE_URL: config.VITE_API_BASE_URL || 'http://localhost:8080',
    ENDPOINTS: {
        AUTHENTICATE: '/authentication/authenticate',
        REGISTER: '/authentication/register',
        GET_CURRENT_USER: '/authentication/me',
        UPDATE_USER: '/user-management/:user_id',
        REDIRECT_LOGIN_GOOGLE: '/authentication/oauth2/authorization/google?redirect-uri=' + (config.VITE_FRONTEND_URL || 'http://localhost:3000') + '/dashboard',
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
        EXCEL_GET_SHEETS: '/data-processing/excel/sheets',
        SOURCE_CONFIRM_SHEET: '/reporting/sources/:id/confirm-sheet',
        CHARTS: '/reporting/charts',
        REPORTS: '/reporting/reports',
        USER_MANAGEMENT: '/user-management',
        NOTIFICATIONS: '/integrated/notifications'
    }
} as const;

// Environment-specific API configuration
export const getApiConfig = () => {
    return {
        baseUrl: API_CONFIG.BASE_URL,
        timeout: config.VITE_APP_ENV === 'production' ? 30000 : 10000,
        retries: config.VITE_APP_ENV === 'production' ? 3 : 1,
        enableLogging: config.VITE_DEBUG === 'true',
        enableCache: config.VITE_APP_ENV === 'production'
    };
};
