.PHONY: up down clean clean-all logs ps

# Default target
all: up

# Start the containers
up:
	docker compose up

# Stop the containers
down:
	docker compose down
	sudo rm -r docker/mysql-server/data
	docker image rm cdp-for-service-app:latest

# View logs
logs:
	docker compose logs -f

# List containers
ps:
	docker compose ps

# Clean up unnecessary files and folders
clean:
	

# Clean everything including Docker resources
clean-all: clean down-v
	docker system prune -f
	docker volume prune -f

# Rebuild and restart containers
rebuild:
	docker compose down
	docker compose build --no-cache
	docker compose up

# Show help
help:
	@echo "Available commands:"
	@echo "  make up        - Start containers"
	@echo "  make down      - Stop containers"
	@echo "  make logs      - View container logs"
	@echo "  make ps        - List containers"
	@echo "  make clean     - Clean up unnecessary files and folders"
	@echo "  make clean-all - Clean everything including Docker resources"
	@echo "  make rebuild   - Rebuild and restart containers"
