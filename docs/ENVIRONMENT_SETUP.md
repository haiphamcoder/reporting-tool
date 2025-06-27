# Environment Setup Guide

## Overview

This project uses different environment files for different purposes:

- `env.ci.example` - Used for CI/CD testing and validation
- `.env.prod` - Used for production deployment (contains real production values)
- `.env` - The actual environment file used by the application (created from either of the above)

## File Purposes

### env.ci.example
- **Purpose**: CI/CD testing and validation
- **Content**: Dummy/test values for syntax checking
- **Usage**: Automatically copied to `.env` during CI/CD pipeline testing
- **Security**: Safe to commit to repository (contains no real secrets)

### .env.prod
- **Purpose**: Production deployment configuration
- **Content**: Real production environment variables and secrets
- **Usage**: Automatically copied to `.env` during production deployment
- **Security**: **NEVER commit to repository** (contains real secrets)

### .env
- **Purpose**: Active environment file used by the application
- **Content**: Copied from either `env.ci.example` or `.env.prod`
- **Usage**: Read by Docker Compose and application containers
- **Security**: Should be in `.gitignore`

## Deployment Flow

### CI/CD Pipeline (Testing)
1. GitHub Actions workflow runs
2. `env.ci.example` is copied to `.env`
3. Docker Compose syntax is validated
4. Tests are run with dummy values
5. No actual deployment occurs

### Production Deployment
1. Code is pushed to `main` branch
2. GitHub Actions triggers deployment to production server
3. SSH to server and navigate to `./projects/reporting-tool`
4. Fetch and pull latest code: `git fetch origin main && git pull origin main`
5. Stop old containers: `docker compose -f docker-compose.prod.yml down`
6. Clean old images: `make clean-images`
7. Start new containers: `docker compose -f docker-compose.prod.yml up -d`
8. Application runs with existing `.env` file (no environment file copying needed)

## Setup Instructions

### For Development
```bash
# Copy the example file for local development
cp env.ci.example .env

# Edit .env with your local development values
nano .env
```

### For Production Server
```bash
# Ensure .env file exists with real production values
# (This file should be manually created on the server)

# The deployment process will automatically:
# 1. Fetch and pull latest code
# 2. Stop old containers
# 3. Clean old images
# 4. Start new containers
```

### For CI/CD Testing
```bash
# The CI/CD pipeline automatically uses env.ci.example
# No manual setup required
```

## Security Best Practices

1. **Never commit `.env.prod` to the repository**
2. **Always use dummy values in `env.ci.example`**
3. **Keep `.env` in `.gitignore`**
4. **Use environment variables for sensitive data in production**
5. **Rotate secrets regularly**

## Troubleshooting

### Missing .env Error
If you see an error about missing `.env` during deployment:
```bash
# Create .env with your production values
cp env.ci.example .env
# Edit .env with real production values
nano .env
```

### Environment Variables Not Loading
If environment variables are not being loaded:
```bash
# Check if .env file exists
ls -la .env

# Manually copy the appropriate file
cp env.ci.example .env  # for development/testing
```

### Deployment Issues
If deployment fails:
```bash
# Check container status
docker compose -f docker-compose.prod.yml ps

# Check logs
docker compose -f docker-compose.prod.yml logs

# Manual deployment steps
git fetch origin main
git pull origin main
docker compose -f docker-compose.prod.yml down
make clean-images
docker compose -f docker-compose.prod.yml up -d
```

## File Structure Example

```
reporting-tool/
├── env.ci.example          # CI/CD testing (committed)
├── .env.prod              # Production values (not committed)
├── .env                   # Active environment (not committed)
├── .gitignore             # Excludes .env and .env.prod
└── scripts/
    ├── deploy.sh          # Uses .env.prod for production
    └── setup-environment.sh # Uses .env.prod for production
``` 