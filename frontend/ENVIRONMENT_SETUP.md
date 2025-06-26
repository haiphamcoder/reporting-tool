# Frontend Environment Configuration

This document explains how to configure and run the frontend application in different environments.

## Environment Overview

The frontend application now supports multiple environments with separate configurations:

- **Local Development**: For development and testing
- **Production**: For production deployment

## Configuration Files

### Environment Configuration Files

- `config/env.local.js` - Local development configuration
- `config/env.production.js` - Production configuration
- `config/env.js` - Main configuration manager

### Docker Files

- `Dockerfile.local` - Docker configuration for local environment
- `Dockerfile.production` - Docker configuration for production environment
- `Dockerfile` - Original Dockerfile (kept for backward compatibility)

## Available Scripts

### Development Scripts

```bash
# Start development server (local environment)
npm run dev
npm run dev:local

# Start development server (production environment simulation)
npm run dev:prod

# Start development server (default)
npm start
```

### Build Scripts

```bash
# Build for production (default)
npm run build

# Build for local environment
npm run build:local

# Build for production environment
npm run build:prod
```

### Preview Scripts

```bash
# Preview production build
npm run preview

# Preview local build
npm run preview:local

# Preview production build
npm run preview:prod
```

### Utility Scripts

```bash
# Lint code
npm run lint
npm run lint:fix

# Type checking
npm run type-check

# Clean build artifacts
npm run clean
```

## Environment Variables

### Local Environment Variables

```javascript
// config/env.local.js
{
  VITE_API_BASE_URL: 'http://localhost:8080',
  VITE_FRONTEND_URL: 'http://localhost:3000',
  VITE_APP_ENV: 'local',
  VITE_DEBUG: 'true',
  VITE_LOG_LEVEL: 'debug',
  VITE_ENABLE_ANALYTICS: 'false',
  VITE_ENABLE_DEBUG_PANEL: 'true'
}
```

### Production Environment Variables

```javascript
// config/env.production.js
{
  VITE_API_BASE_URL: 'https://api.yourdomain.com',
  VITE_FRONTEND_URL: 'https://yourdomain.com',
  VITE_APP_ENV: 'production',
  VITE_DEBUG: 'false',
  VITE_LOG_LEVEL: 'error',
  VITE_ENABLE_ANALYTICS: 'true',
  VITE_ENABLE_DEBUG_PANEL: 'false'
}
```

## Docker Compose Integration

### Local Environment

The frontend is included in `docker-compose.local.yml`:

```yaml
frontend:
  build:
    context: ./frontend
    dockerfile: Dockerfile.local
  container_name: frontend-local
  environment:
    - NODE_ENV=local
    - VITE_APP_ENV=local
    - VITE_API_BASE_URL=http://localhost:8080
    - VITE_FRONTEND_URL=http://localhost:3000
  ports:
    - "3000:80"
  depends_on:
    - api-gateway
```

### Production Environment

The frontend is included in `docker-compose.prod.yml`:

```yaml
frontend:
  build:
    context: ./frontend
    dockerfile: Dockerfile.production
  container_name: frontend-production
  environment:
    - NODE_ENV=production
    - VITE_APP_ENV=production
    - VITE_API_BASE_URL=${VITE_API_BASE_URL:-https://api.yourdomain.com}
    - VITE_FRONTEND_URL=${VITE_FRONTEND_URL:-https://yourdomain.com}
  ports:
    - "3000:80"
  depends_on:
    - api-gateway
  healthcheck:
    test: ["CMD", "curl", "-f", "http://localhost/"]
    interval: 30s
    timeout: 10s
    retries: 3
```

## Running the Application

### Using Docker Compose

```bash
# Start local environment
./test-environments.sh start-local

# Start production environment
./test-environments.sh start-prod

# View logs
./test-environments.sh logs-local frontend
./test-environments.sh logs-prod frontend
```

### Using npm directly

```bash
# Development (local environment)
npm run dev:local

# Development (production environment simulation)
npm run dev:prod

# Build for production
npm run build:prod

# Preview production build
npm run preview:prod
```

## Configuration Usage in Code

### Using the Configuration System

```javascript
import { config, isProduction, isLocal } from '../config/env.js';

// Access configuration values
const apiUrl = config.VITE_API_BASE_URL;
const isDebug = config.VITE_DEBUG === 'true';

// Check environment
if (isProduction()) {
  // Production-specific code
}

if (isLocal()) {
  // Local-specific code
}
```

### API Configuration

The API configuration automatically uses the environment-specific settings:

```javascript
import { API_CONFIG, getApiConfig } from '../config/api';

// API_CONFIG.BASE_URL is automatically set based on environment
const apiConfig = getApiConfig();
```

## Environment-Specific Features

### Local Environment Features

- Hot reload enabled
- Source maps enabled
- Debug panel enabled
- Detailed logging
- Development optimizations disabled

### Production Environment Features

- Hot reload disabled
- Source maps disabled
- Debug panel disabled
- Error-level logging only
- Code splitting and optimization
- Security headers
- Health checks
- Non-root user execution

## Troubleshooting

### Common Issues

1. **Environment variables not loading**: Make sure you're using the correct mode flag (`--mode local` or `--mode production`)

2. **API calls failing**: Check that `VITE_API_BASE_URL` is correctly set for your environment

3. **Build errors**: Run `npm run clean` to clear cached files

4. **Docker build issues**: Make sure you're using the correct Dockerfile for your environment

### Debug Mode

Enable debug mode by setting `VITE_DEBUG=true` in your environment configuration. This will provide additional logging and debugging information.

## Migration from Old Configuration

If you were previously using environment variables directly, update your code to use the new configuration system:

```javascript
// Old way
const apiUrl = import.meta.env.VITE_API_BASE_URL;

// New way
import { config } from '../config/env.js';
const apiUrl = config.VITE_API_BASE_URL;
``` 