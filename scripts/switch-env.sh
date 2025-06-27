#!/bin/bash

# Environment switching script
# This script helps switch between different environment configurations

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Logging function
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] WARNING: $1${NC}"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}"
}

info() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')] INFO: $1${NC}"
}

# Function to backup current .env
backup_env() {
    if [ -f ".env" ]; then
        log "Backing up current .env file..."
        cp .env .env.backup.$(date +%Y%m%d_%H%M%S)
        log "Backup created: .env.backup.$(date +%Y%m%d_%H%M%S)"
    fi
}

# Function to switch to development environment
switch_to_dev() {
    log "Switching to development environment..."
    
    backup_env
    
    if [ -f "env.ci.example" ]; then
        cp env.ci.example .env
        log "✅ Switched to development environment (using env.ci.example)"
        info "Remember to edit .env with your local development values"
    else
        error "env.ci.example not found"
        exit 1
    fi
}

# Function to switch to production environment
switch_to_prod() {
    log "Switching to production environment..."
    
    backup_env
    
    if [ -f ".env.prod" ]; then
        cp .env.prod .env
        log "✅ Switched to production environment (using .env.prod)"
    else
        error ".env.prod not found"
        error "Please create .env.prod with your production values first"
        exit 1
    fi
}

# Function to show current environment
show_current() {
    if [ ! -f ".env" ]; then
        warn "No .env file found"
        return
    fi
    
    log "Current environment file (.env):"
    echo ""
    
    # Show first few lines of .env (without sensitive data)
    head -10 .env | while IFS= read -r line; do
        if [[ $line =~ ^[[:space:]]*# ]]; then
            # Comments
            echo -e "${BLUE}$line${NC}"
        elif [[ $line =~ ^[[:space:]]*$ ]]; then
            # Empty lines
            echo "$line"
        else
            # Environment variables - show key only if it contains sensitive data
            if [[ $line =~ (PASSWORD|SECRET|TOKEN|KEY)= ]]; then
                key=$(echo "$line" | cut -d'=' -f1)
                echo -e "${YELLOW}$key=***HIDDEN***${NC}"
            else
                echo "$line"
            fi
        fi
    done
    
    echo ""
    
    # Check which source file was used
    if [ -f ".env.prod" ] && [ -f "env.ci.example" ]; then
        if cmp -s .env .env.prod; then
            info "Current .env is based on .env.prod (production)"
        elif cmp -s .env env.ci.example; then
            info "Current .env is based on env.ci.example (development)"
        else
            warn "Current .env differs from both .env.prod and env.ci.example"
        fi
    fi
}

# Function to create .env.prod from template
create_prod_env() {
    log "Creating .env.prod from template..."
    
    if [ -f ".env.prod" ]; then
        warn ".env.prod already exists"
        read -p "Do you want to overwrite it? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            log "Operation cancelled"
            return
        fi
    fi
    
    if [ -f "env.ci.example" ]; then
        cp env.ci.example .env.prod
        log "✅ .env.prod created from env.ci.example"
        warn "Please edit .env.prod with your production values"
        info "You can use: nano .env.prod"
    else
        error "env.ci.example not found"
        exit 1
    fi
}

# Function to validate environment file
validate_env() {
    local env_file="${1:-.env}"
    
    if [ ! -f "$env_file" ]; then
        error "Environment file not found: $env_file"
        return 1
    fi
    
    log "Validating $env_file..."
    
    # Check for required variables
    required_vars=(
        "MYSQL_ROOT_PASSWORD"
        "MYSQL_USER"
        "MYSQL_PASSWORD"
        "MYSQL_DATABASE"
        "JWT_SECRET_KEY"
    )
    
    missing_vars=()
    
    for var in "${required_vars[@]}"; do
        if ! grep -q "^${var}=" "$env_file"; then
            missing_vars+=("$var")
        fi
    done
    
    if [ ${#missing_vars[@]} -ne 0 ]; then
        error "Missing required variables in $env_file:"
        for var in "${missing_vars[@]}"; do
            echo "  - $var"
        done
        return 1
    fi
    
    log "✅ $env_file validation passed"
    return 0
}

# Function to show help
show_help() {
    echo "Environment Switching Script"
    echo ""
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  dev          Switch to development environment (env.ci.example)"
    echo "  prod         Switch to production environment (.env.prod)"
    echo "  current      Show current environment configuration"
    echo "  create-prod  Create .env.prod from env.ci.example template"
    echo "  validate     Validate current .env file"
    echo "  help         Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 dev           # Switch to development"
    echo "  $0 prod          # Switch to production"
    echo "  $0 current       # Show current config"
    echo "  $0 create-prod   # Create production template"
    echo ""
}

# Main script logic
case "${1:-help}" in
    "dev"|"development")
        switch_to_dev
        ;;
    "prod"|"production")
        switch_to_prod
        ;;
    "current"|"show")
        show_current
        ;;
    "create-prod"|"create")
        create_prod_env
        ;;
    "validate"|"check")
        validate_env
        ;;
    "help"|"--help"|"-h"|"")
        show_help
        ;;
    *)
        error "Unknown command: $1"
        echo ""
        show_help
        exit 1
        ;;
esac 