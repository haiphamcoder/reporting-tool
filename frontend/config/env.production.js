// Production Environment Configuration
export const productionConfig = {
  // Frontend Configuration
  VITE_API_BASE_URL: 'http://api.reporting-tool.site',
  VITE_FRONTEND_URL: 'http://reporting-tool.site',
  
  // Production Settings
  VITE_APP_ENV: 'production',
  VITE_DEBUG: 'false',
  VITE_LOG_LEVEL: 'error',
  
  // Feature Flags
  VITE_ENABLE_ANALYTICS: 'true',
  VITE_ENABLE_DEBUG_PANEL: 'false',
  
  // API Endpoints
  API_GATEWAY_URL: 'http://api.reporting-tool.site',
  EUREKA_URL: 'http://eureka.reporting-tool.site',
  
  // Production Optimizations
  ENABLE_HOT_RELOAD: false,
  ENABLE_SOURCE_MAPS: false,
  ENABLE_ESLINT: false,
  
  // Performance Settings
  ENABLE_COMPRESSION: true,
  ENABLE_CACHING: true,
  ENABLE_CDN: true
}; 