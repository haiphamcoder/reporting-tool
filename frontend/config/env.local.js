// Local Development Environment Configuration
export const localConfig = {
  // Frontend Configuration
  VITE_API_BASE_URL: 'http://localhost:8765',
  VITE_FRONTEND_URL: 'http://localhost:3000',
  
  // Development Settings
  VITE_APP_ENV: 'local',
  VITE_DEBUG: 'true',
  VITE_LOG_LEVEL: 'debug',
  
  // Feature Flags
  VITE_ENABLE_ANALYTICS: 'false',
  VITE_ENABLE_DEBUG_PANEL: 'true',
  
  // API Endpoints
  API_GATEWAY_URL: 'http://localhost:8765',
  EUREKA_URL: 'http://localhost:8761',
  
  // Development Tools
  ENABLE_HOT_RELOAD: true,
  ENABLE_SOURCE_MAPS: true,
  ENABLE_ESLINT: true
}; 