#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}================================${NC}"
}

# Function to check if Docker is running
check_docker() {
    if ! docker info >/dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker first."
        exit 1
    fi
    print_status "Docker is running"
}

# Function to check if required files exist
check_files() {
    local env=$1
    local compose_file="docker-compose.$env.yml"
    
    if [ ! -f "$compose_file" ]; then
        print_error "File $compose_file not found!"
        exit 1
    fi
    
    print_status "Found $compose_file"
}

# Function to stop all containers
stop_all() {
    print_header "Stopping all containers"
    
    # Stop local environment
    print_status "Stopping local environment..."
    docker compose -f docker-compose.local.yml down --remove-orphans 2>/dev/null
    docker compose -f docker-compose.prod.yml down --remove-orphans 2>/dev/null
    
    print_status "All containers stopped"
}

# Function to start local environment
start_local() {
    print_header "Starting Local Environment"
    
    check_docker
    check_files "local"
    
    # Stop any existing containers first
    print_status "Stopping existing containers..."
    docker compose -f docker-compose.local.yml down --remove-orphans 2>/dev/null
    
    # Start services
    print_status "Starting local environment..."
    docker compose -f docker-compose.local.yml up --build -d
    
    # Wait a moment for services to start
    sleep 5
    
    # Show status
    print_status "Local environment status:"
    docker compose -f docker-compose.local.yml ps
    
    print_status "Local environment started successfully!"
    print_status "You can access the application at: http://localhost:3000"
    print_status "API Gateway: http://localhost:8080"
    print_status "Eureka Discovery: http://localhost:8761"
}

# Function to start production environment
start_prod() {
    print_header "Starting Production Environment"
    
    check_docker
    check_files "prod"
    
    # Stop any existing containers first
    print_status "Stopping existing containers..."
    docker compose -f docker-compose.prod.yml down --remove-orphans 2>/dev/null
    
    # Start services
    print_status "Starting production environment..."
    docker compose -f docker-compose.prod.yml up --build -d
    
    # Wait a moment for services to start
    sleep 5
    
    # Show status
    print_status "Production environment status:"
    docker compose -f docker-compose.prod.yml ps
    
    print_status "Production environment started successfully!"
    print_status "You can access the application at: http://localhost:3000"
    print_status "API Gateway: http://localhost:8080"
    print_status "Eureka Discovery: http://localhost:8761"
}

# Function to show logs
show_logs() {
    local env=$1
    local service=$2
    
    if [ -z "$env" ]; then
        print_error "Please specify environment (local or prod)"
        exit 1
    fi
    
    check_files "$env"
    
    if [ -z "$service" ]; then
        print_status "Showing logs for all services in $env environment:"
        docker compose -f docker-compose.$env.yml logs -f
    else
        print_status "Showing logs for $service in $env environment:"
        docker compose -f docker-compose.$env.yml logs -f $service
    fi
}

# Function to stop specific environment
stop_env() {
    local env=$1
    
    if [ -z "$env" ]; then
        print_error "Please specify environment (local or prod)"
        exit 1
    fi
    
    check_files "$env"
    
    print_status "Stopping $env environment..."
    docker compose -f docker-compose.$env.yml down
    print_status "$env environment stopped"
}

# Function to show help
show_help() {
    echo "Usage: $0 [COMMAND] [OPTIONS]"
    echo ""
    echo "Commands:"
    echo "  start-local     Start local development environment"
    echo "  start-prod      Start production environment"
    echo "  stop-all        Stop all containers"
    echo "  stop-local      Stop local environment"
    echo "  stop-prod       Stop production environment"
    echo "  logs-local      Show logs for local environment"
    echo "  logs-prod       Show logs for production environment"
    echo "  logs-local [service]  Show logs for specific service in local environment"
    echo "  logs-prod [service]   Show logs for specific service in production environment"
    echo "  help            Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 start-local"
    echo "  $0 start-prod"
    echo "  $0 logs-local api-gateway"
    echo "  $0 logs-prod frontend"
}

# Main script logic
case "$1" in
    "start-local")
        start_local
        ;;
    "start-prod")
        start_prod
        ;;
    "stop-all")
        stop_all
        ;;
    "stop-local")
        stop_env "local"
        ;;
    "stop-prod")
        stop_env "prod"
        ;;
    "logs-local")
        show_logs "local" "$2"
        ;;
    "logs-prod")
        show_logs "prod" "$2"
        ;;
    "help"|"-h"|"--help"|"")
        show_help
        ;;
    *)
        print_error "Unknown command: $1"
        echo ""
        show_help
        exit 1
        ;;
esac 