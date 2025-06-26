#!/bin/bash

# Setup environment script for Google Cloud VM
# This script ensures the environment is properly configured

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
PROJECT_PATH="${PROJECT_PATH:-$(pwd)}"
REPO_URL="${REPO_URL:-https://github.com/your-username/reporting-tool.git}"

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

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to check and install Docker
check_docker() {
    log "Checking Docker installation..."
    
    if ! command_exists docker; then
        error "Docker is not installed"
        log "Installing Docker..."
        curl -fsSL https://get.docker.com -o get-docker.sh
        sudo sh get-docker.sh
        sudo usermod -aG docker $USER
        log "Docker installed successfully"
    else
        log "✅ Docker is already installed"
    fi
    
    if ! command_exists docker-compose; then
        warn "Docker Compose is not installed"
        log "Installing Docker Compose..."
        sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
        sudo chmod +x /usr/local/bin/docker-compose
        log "Docker Compose installed successfully"
    else
        log "✅ Docker Compose is already installed"
    fi
}

# Function to check and install Git
check_git() {
    log "Checking Git installation..."
    
    if ! command_exists git; then
        error "Git is not installed"
        log "Installing Git..."
        sudo apt-get update
        sudo apt-get install -y git
        log "Git installed successfully"
    else
        log "✅ Git is already installed"
    fi
}

# Function to check and install Make
check_make() {
    log "Checking Make installation..."
    
    if ! command_exists make; then
        error "Make is not installed"
        log "Installing Make..."
        sudo apt-get update
        sudo apt-get install -y make
        log "Make installed successfully"
    else
        log "✅ Make is already installed"
    fi
}

# Function to check and setup project
check_project() {
    log "Checking project setup..."
    
    if [ ! -d "$PROJECT_PATH" ]; then
        error "Project directory does not exist: $PROJECT_PATH"
        log "Creating project directory..."
        mkdir -p "$PROJECT_PATH"
        cd "$PROJECT_PATH"
        
        log "Cloning repository..."
        git clone "$REPO_URL" .
    else
        log "✅ Project directory exists: $PROJECT_PATH"
        cd "$PROJECT_PATH"
        
        # Check if it's a git repository
        if [ ! -d ".git" ]; then
            error "Project directory is not a git repository"
            log "Initializing git repository..."
            git init
            git remote add origin "$REPO_URL"
        fi
        
        # Pull latest code
        log "Pulling latest code..."
        git pull origin main || git fetch origin main
    fi
}

# Function to check required files
check_required_files() {
    log "Checking required files..."
    
    required_files=(
        "docker-compose.prod.yml"
        "Makefile"
        "scripts/deploy.sh"
        "scripts/test-connection.sh"
        "frontend/package.json"
        "frontend/package-lock.json"
        "env.ci.example"
    )
    
    missing_files=()
    
    for file in "${required_files[@]}"; do
        if [ ! -f "$file" ]; then
            missing_files+=("$file")
        else
            log "✅ $file exists"
        fi
    done
    
    if [ ${#missing_files[@]} -ne 0 ]; then
        error "Missing required files:"
        for file in "${missing_files[@]}"; do
            echo "  - $file"
        done
        return 1
    fi
    
    log "✅ All required files exist"
}

# Function to setup environment file
setup_environment() {
    log "Setting up environment file..."
    
    if [ ! -f ".env" ]; then
        if [ -f "env.ci.example" ]; then
            log "Creating .env from env.ci.example..."
            cp env.ci.example .env
            warn "Please edit .env file with your production values"
        else
            error "No env.ci.example found"
            return 1
        fi
    else
        log "✅ .env file already exists"
    fi
}

# Function to setup file permissions
setup_permissions() {
    log "Setting up file permissions..."
    
    # Make scripts executable
    chmod +x scripts/deploy.sh
    chmod +x scripts/test-connection.sh
    
    log "✅ File permissions set"
}

# Function to check firewall
check_firewall() {
    log "Checking firewall configuration..."
    
    # Check if ufw is active
    if command_exists ufw; then
        if sudo ufw status | grep -q "Status: active"; then
            log "✅ UFW firewall is active"
            
            # Check if required ports are open
            required_ports=(22 80 8761 8765 8091 8092 8093 8094 8095)
            
            for port in "${required_ports[@]}"; do
                if sudo ufw status | grep -q "$port"; then
                    log "✅ Port $port is open"
                else
                    warn "Port $port might not be open"
                fi
            done
        else
            warn "UFW firewall is not active"
        fi
    else
        warn "UFW is not installed"
    fi
}

# Function to test basic functionality
test_basic_functionality() {
    log "Testing basic functionality..."
    
    # Test Docker
    if docker --version > /dev/null 2>&1; then
        log "✅ Docker is working"
    else
        error "❌ Docker is not working"
        return 1
    fi
    
    # Test Docker Compose
    if docker compose version > /dev/null 2>&1; then
        log "✅ Docker Compose is working"
    else
        error "❌ Docker Compose is not working"
        return 1
    fi
    
    # Test Git
    if git --version > /dev/null 2>&1; then
        log "✅ Git is working"
    else
        error "❌ Git is not working"
        return 1
    fi
    
    # Test Make
    if make --version > /dev/null 2>&1; then
        log "✅ Make is working"
    else
        error "❌ Make is not working"
        return 1
    fi
}

# Main function
main() {
    log "Starting environment setup..."
    
    # Check and install required tools
    check_docker
    check_git
    check_make
    
    # Setup project
    check_project
    
    # Check required files
    check_required_files
    
    # Setup environment
    setup_environment
    
    # Setup permissions
    setup_permissions
    
    # Check firewall
    check_firewall
    
    # Test functionality
    test_basic_functionality
    
    log "Environment setup completed successfully!"
    log ""
    log "Next steps:"
    log "1. Edit .env file with your production values"
    log "2. Run: ./scripts/test-connection.sh"
    log "3. Run: ./scripts/deploy.sh deploy"
}

# Handle script arguments
case "${1:-setup}" in
    "setup")
        main
        ;;
    "check")
        check_project
        check_required_files
        test_basic_functionality
        ;;
    *)
        echo "Usage: $0 {setup|check}"
        echo "  setup - Setup the complete environment"
        echo "  check - Check if environment is properly configured"
        exit 1
        ;;
esac 