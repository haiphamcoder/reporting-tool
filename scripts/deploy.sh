#!/bin/bash

# Deployment script for reporting-tool
# This script handles simple deployment: fetch/pull code, stop containers, clean images, start containers

set -e  # Exit on any error

# Configuration
PROJECT_NAME="reporting-tool"
COMPOSE_FILE="docker-compose.prod.yml"
LOG_FILE="/tmp/deployment.log"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Logging function
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}" | tee -a $LOG_FILE
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] WARNING: $1${NC}" | tee -a $LOG_FILE
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}" | tee -a $LOG_FILE
}

# Function to check if services are healthy
check_services_health() {
    log "Checking services health..."
    
    # Wait for services to start
    sleep 10
    
    # Check if containers are running
    if ! docker compose -f $COMPOSE_FILE ps | grep -q "Up"; then
        error "Some containers are not running"
        return 1
    fi
    
    # Check Eureka Discovery Server
    log "Checking Eureka Discovery Server..."
    for i in {1..30}; do
        if curl -f http://localhost:8761/actuator/health > /dev/null 2>&1; then
            log "Eureka Discovery Server is healthy"
            break
        fi
        if [ $i -eq 30 ]; then
            error "Eureka Discovery Server health check failed"
            return 1
        fi
        sleep 2
    done
    
    # Check API Gateway
    log "Checking API Gateway..."
    for i in {1..30}; do
        if curl -f http://localhost:8765/actuator/health > /dev/null 2>&1; then
            log "API Gateway is healthy"
            break
        fi
        if [ $i -eq 30 ]; then
            error "API Gateway health check failed"
            return 1
        fi
        sleep 2
    done
    
    # Check Frontend
    log "Checking Frontend..."
    for i in {1..30}; do
        if curl -f http://localhost:80 > /dev/null 2>&1; then
            log "Frontend is accessible"
            break
        fi
        if [ $i -eq 30 ]; then
            error "Frontend health check failed"
            return 1
        fi
        sleep 2
    done
    
    log "All services are healthy!"
    return 0
}

# Main deployment function
deploy() {
    log "Starting deployment process..."
    
    # Fetch and pull latest code
    log "Fetching latest code..."
    git fetch origin main
    
    log "Checking out latest code..."
    git checkout main
    
    log "Pulling latest code..."
    git pull origin main
    
    # Stop old containers
    log "Stopping old containers..."
    docker compose -f $COMPOSE_FILE down
    
    # Wait for containers to stop completely
    log "Waiting for containers to stop..."
    sleep 10
    
    # Clean old images
    log "Cleaning old images..."
    make clean-images
    
    # Start new containers
    log "Starting new containers..."
    docker compose -f $COMPOSE_FILE up -d --build
    
    # Wait for containers to start
    log "Waiting for containers to start..."
    sleep 15
    
    # Check services health
    if check_services_health; then
        log "Deployment completed successfully!"
        
        # Show final status
        log "Final container status:"
        docker compose -f $COMPOSE_FILE ps
        
        return 0
    else
        error "Deployment failed - services are not healthy"
        exit 1
    fi
}

# Handle script arguments
case "${1:-deploy}" in
    "deploy")
        deploy
        ;;
    "status")
        log "Current deployment status:"
        docker compose -f $COMPOSE_FILE ps
        echo ""
        log "Service health checks:"
        check_services_health
        ;;
    "logs")
        log "Recent deployment logs:"
        docker compose -f $COMPOSE_FILE logs --tail=50
        ;;
    *)
        echo "Usage: $0 {deploy|status|logs}"
        echo "  deploy   - Deploy the application (fetch/pull code, stop containers, clean images, start containers)"
        echo "  status   - Check current deployment status"
        echo "  logs     - Show recent logs"
        exit 1
        ;;
esac 