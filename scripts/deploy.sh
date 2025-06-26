#!/bin/bash

# Deployment script for reporting-tool
# This script handles deployment with rollback capability

set -e  # Exit on any error

# Configuration
PROJECT_NAME="reporting-tool"
COMPOSE_FILE="docker-compose.prod.yml"
BACKUP_DIR="/tmp/reporting-tool-backup"
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

# Function to backup current deployment
backup_current_deployment() {
    log "Creating backup of current deployment..."
    
    # Create backup directory
    mkdir -p $BACKUP_DIR
    
    # Backup docker-compose file
    cp $COMPOSE_FILE $BACKUP_DIR/
    
    # Backup current git commit
    git rev-parse HEAD > $BACKUP_DIR/current_commit.txt
    
    # Backup current images
    docker images | grep $PROJECT_NAME > $BACKUP_DIR/current_images.txt
    
    log "Backup created successfully"
}

# Function to rollback deployment
rollback_deployment() {
    error "Rolling back deployment..."
    
    # Stop current containers
    log "Stopping current containers..."
    docker compose -f $COMPOSE_FILE down || true
    
    # Clean current images
    log "Cleaning current images..."
    make clean-images || true
    
    # Restore from backup if available
    if [ -f "$BACKUP_DIR/current_commit.txt" ]; then
        log "Restoring from backup..."
        git checkout $(cat $BACKUP_DIR/current_commit.txt)
        
        # Rebuild and start containers
        log "Rebuilding and starting containers..."
        docker compose -f $COMPOSE_FILE up -d --build
        
        # Check health
        if check_services_health; then
            log "Rollback completed successfully"
        else
            error "Rollback failed - services are not healthy"
            exit 1
        fi
    else
        error "No backup available for rollback"
        exit 1
    fi
}

# Main deployment function
deploy() {
    log "Starting deployment process..."
    
    # Create backup
    backup_current_deployment
    
    # Stop old containers
    log "Stopping old containers..."
    docker compose -f $COMPOSE_FILE down
    
    # Clean old images
    log "Cleaning old images..."
    make clean-images
    
    # Pull latest code
    log "Pulling latest code..."
    git pull origin main
    
    # Start new containers
    log "Starting new containers..."
    docker compose -f $COMPOSE_FILE up -d --build
    
    # Check services health
    if check_services_health; then
        log "Deployment completed successfully!"
        
        # Clean up backup
        rm -rf $BACKUP_DIR
        
        # Show final status
        log "Final container status:"
        docker compose -f $COMPOSE_FILE ps
        
        return 0
    else
        error "Deployment failed - services are not healthy"
        rollback_deployment
        return 1
    fi
}

# Handle script arguments
case "${1:-deploy}" in
    "deploy")
        deploy
        ;;
    "rollback")
        rollback_deployment
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
        echo "Usage: $0 {deploy|rollback|status|logs}"
        echo "  deploy   - Deploy the application"
        echo "  rollback - Rollback to previous deployment"
        echo "  status   - Check current deployment status"
        echo "  logs     - Show recent logs"
        exit 1
        ;;
esac 