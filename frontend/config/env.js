// Environment Configuration Manager
import { localConfig } from './env.local.js';
import { productionConfig } from './env.production.js';

// Determine current environment
const getEnvironment = () => {
  // Check for explicit environment variable
  if (import.meta.env.VITE_APP_ENV) {
    return import.meta.env.VITE_APP_ENV;
  }
  
  // Check for NODE_ENV
  if (import.meta.env.MODE === 'production') {
    return 'production';
  }
  
  // Default to development (local)
  return 'development';
};

// Get configuration based on environment
export const getConfig = () => {
  const env = getEnvironment();
  
  switch (env) {
    case 'production':
      return productionConfig;
    case 'development':
    case 'local':
    default:
      return localConfig;
  }
};

// Export current configuration
export const config = getConfig();

// Export environment helper
export const isProduction = () => getEnvironment() === 'production';
export const isDevelopment = () => getEnvironment() === 'development' || getEnvironment() === 'local';
export const isLocal = () => isDevelopment();

// Export individual configs for direct access
export { localConfig, productionConfig }; 