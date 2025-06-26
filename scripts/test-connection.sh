#!/bin/bash

# Test SSH connection script
# This script tests the SSH connection to the Google Cloud VM

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
HOST="${HOST:-}"
USERNAME="${USERNAME:-}"
SSH_KEY="${SSH_KEY:-}"
PORT="${PORT:-22}"
PROJECT_PATH="${PROJECT_PATH:-}"

# Function to print colored output
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] WARNING: $1${NC}"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}"
}

# Function to check if required variables are set
check_variables() {
    local missing_vars=()
    
    if [ -z "$HOST" ]; then
        missing_vars+=("HOST")
    fi
    
    if [ -z "$USERNAME" ]; then
        missing_vars+=("USERNAME")
    fi
    
    if [ -z "$SSH_KEY" ]; then
        missing_vars+=("SSH_KEY")
    fi
    
    if [ -z "$PROJECT_PATH" ]; then
        missing_vars+=("PROJECT_PATH")
    fi
    
    if [ ${#missing_vars[@]} -ne 0 ]; then
        error "Missing required environment variables: ${missing_vars[*]}"
        echo "Please set the following environment variables:"
        for var in "${missing_vars[@]}"; do
            echo "  export $var=\"your_value\""
        done
        exit 1
    fi
}

# Function to create temporary SSH key file
create_ssh_key_file() {
    local temp_key_file="/tmp/github_actions_key"
    
    # Create temporary SSH key file
    echo "$SSH_KEY" > "$temp_key_file"
    chmod 600 "$temp_key_file"
    
    echo "$temp_key_file"
}

# Function to test SSH connection
test_ssh_connection() {
    local ssh_key_file="$1"
    
    log "Testing SSH connection to $USERNAME@$HOST:$PORT"
    
    # Test basic SSH connection
    if ssh -i "$ssh_key_file" -o ConnectTimeout=10 -o StrictHostKeyChecking=no -p "$PORT" "$USERNAME@$HOST" "echo 'SSH connection successful'"; then
        log "✅ SSH connection test passed"
        return 0
    else
        error "❌ SSH connection test failed"
        return 1
    fi
}

# Function to test project directory access
test_project_access() {
    local ssh_key_file="$1"
    
    log "Testing project directory access: $PROJECT_PATH"
    
    # Test if project directory exists
    if ssh -i "$ssh_key_file" -o ConnectTimeout=10 -o StrictHostKeyChecking=no -p "$PORT" "$USERNAME@$HOST" "[ -d \"$PROJECT_PATH\" ]"; then
        log "✅ Project directory exists"
    else
        error "❌ Project directory does not exist: $PROJECT_PATH"
        return 1
    fi
    
    # Test if we can access the directory
    if ssh -i "$ssh_key_file" -o ConnectTimeout=10 -o StrictHostKeyChecking=no -p "$PORT" "$USERNAME@$HOST" "cd \"$PROJECT_PATH\" && pwd"; then
        log "✅ Project directory access test passed"
    else
        error "❌ Cannot access project directory"
        return 1
    fi
}

# Function to test Docker installation
test_docker() {
    local ssh_key_file="$1"
    
    log "Testing Docker installation"
    
    # Test Docker command
    if ssh -i "$ssh_key_file" -o ConnectTimeout=10 -o StrictHostKeyChecking=no -p "$PORT" "$USERNAME@$HOST" "docker --version"; then
        log "✅ Docker is installed"
    else
        error "❌ Docker is not installed or not accessible"
        return 1
    fi
    
    # Test Docker Compose command
    if ssh -i "$ssh_key_file" -o ConnectTimeout=10 -o StrictHostKeyChecking=no -p "$PORT" "$USERNAME@$HOST" "docker compose version"; then
        log "✅ Docker Compose is installed"
    else
        error "❌ Docker Compose is not installed or not accessible"
        return 1
    fi
}

# Function to test Git access
test_git() {
    local ssh_key_file="$1"
    
    log "Testing Git access"
    
    # Test Git command
    if ssh -i "$ssh_key_file" -o ConnectTimeout=10 -o StrictHostKeyChecking=no -p "$PORT" "$USERNAME@$HOST" "git --version"; then
        log "✅ Git is installed"
    else
        error "❌ Git is not installed"
        return 1
    fi
    
    # Test if project is a Git repository
    if ssh -i "$ssh_key_file" -o ConnectTimeout=10 -o StrictHostKeyChecking=no -p "$PORT" "$USERNAME@$HOST" "cd \"$PROJECT_PATH\" && git status"; then
        log "✅ Project is a Git repository"
    else
        error "❌ Project is not a Git repository or cannot access Git"
        return 1
    fi
}

# Function to test Make command
test_make() {
    local ssh_key_file="$1"
    
    log "Testing Make command"
    
    # Test Make command
    if ssh -i "$ssh_key_file" -o ConnectTimeout=10 -o StrictHostKeyChecking=no -p "$PORT" "$USERNAME@$HOST" "make --version"; then
        log "✅ Make is installed"
    else
        error "❌ Make is not installed"
        return 1
    fi
    
    # Test if Makefile exists in project
    if ssh -i "$ssh_key_file" -o ConnectTimeout=10 -o StrictHostKeyChecking=no -p "$PORT" "$USERNAME@$HOST" "cd \"$PROJECT_PATH\" && [ -f Makefile ]"; then
        log "✅ Makefile exists in project"
    else
        error "❌ Makefile does not exist in project"
        return 1
    fi
}

# Function to test deployment script
test_deploy_script() {
    local ssh_key_file="$1"
    
    log "Testing deployment script"
    
    # Test if deployment script exists
    if ssh -i "$ssh_key_file" -o ConnectTimeout=10 -o StrictHostKeyChecking=no -p "$PORT" "$USERNAME@$HOST" "cd \"$PROJECT_PATH\" && [ -f scripts/deploy.sh ]"; then
        log "✅ Deployment script exists"
    else
        error "❌ Deployment script does not exist"
        return 1
    fi
    
    # Test if deployment script is executable
    if ssh -i "$ssh_key_file" -o ConnectTimeout=10 -o StrictHostKeyChecking=no -p "$PORT" "$USERNAME@$HOST" "cd \"$PROJECT_PATH\" && [ -x scripts/deploy.sh ]"; then
        log "✅ Deployment script is executable"
    else
        error "❌ Deployment script is not executable"
        return 1
    fi
}

# Main function
main() {
    log "Starting connection test..."
    
    # Check required variables
    check_variables
    
    # Create temporary SSH key file
    local ssh_key_file
    ssh_key_file=$(create_ssh_key_file)
    
    # Clean up function
    cleanup() {
        rm -f "$ssh_key_file"
    }
    
    # Set trap to clean up on exit
    trap cleanup EXIT
    
    # Run tests
    local tests_passed=0
    local tests_failed=0
    
    # Test SSH connection
    if test_ssh_connection "$ssh_key_file"; then
        ((tests_passed++))
    else
        ((tests_failed++))
    fi
    
    # Test project access
    if test_project_access "$ssh_key_file"; then
        ((tests_passed++))
    else
        ((tests_failed++))
    fi
    
    # Test Docker
    if test_docker "$ssh_key_file"; then
        ((tests_passed++))
    else
        ((tests_failed++))
    fi
    
    # Test Git
    if test_git "$ssh_key_file"; then
        ((tests_passed++))
    else
        ((tests_failed++))
    fi
    
    # Test Make
    if test_make "$ssh_key_file"; then
        ((tests_passed++))
    else
        ((tests_failed++))
    fi
    
    # Test deployment script
    if test_deploy_script "$ssh_key_file"; then
        ((tests_passed++))
    else
        ((tests_failed++))
    fi
    
    # Summary
    echo ""
    log "Connection test summary:"
    echo "  Tests passed: $tests_passed"
    echo "  Tests failed: $tests_failed"
    
    if [ $tests_failed -eq 0 ]; then
        log "✅ All tests passed! The environment is ready for deployment."
        exit 0
    else
        error "❌ Some tests failed. Please fix the issues before deployment."
        exit 1
    fi
}

# Run main function
main "$@" 