# Define required directories
REQUIRED_DIRS := database/hadoop/dfs/namenode database/hadoop/dfs/datanode database/mysql-server/data

# Create directories
init:
	@echo "Creating required directories..."
	@mkdir -p $(REQUIRED_DIRS)
	@chmod 777 $(REQUIRED_DIRS)
	@echo "Directories created successfully!"

# Clean up
clean:
	@echo "Cleaning up..."
	@sudo rm -rf database/hadoop/dfs
	@sudo rm -rf database/mysql-server/data
	@if docker image ls | grep -q reporting-tool-backend; then \
		docker image rm reporting-tool-backend; \
		echo "Removed reporting-tool-backend image"; \
	fi
	@if docker image ls | grep -q reporting-tool-frontend; then \
		docker image rm reporting-tool-frontend; \
		echo "Removed reporting-tool-frontend image"; \
	fi
	@echo "Cleaned up successfully!"

# Docker compose commands
up:
	@echo "Starting containers..."
	docker compose up -d

down:
	@echo "Stopping and removing containers..."
	docker compose down

restart:
	@echo "Restarting containers..."
	docker compose restart

logs:
	@echo "Viewing logs of containers..."
	docker compose logs -f

ps:
	@echo "Listing running containers..."
	docker compose ps

# Display help
help:
	@echo "Available commands:"
	@echo "  make init     : Create required directories"
	@echo "  make clean   : Clean up logs and tmp directories"
	@echo "  make up      : Start docker containers"
	@echo "  make down    : Stop docker containers"
	@echo "  make restart : Restart docker containers"
	@echo "  make logs    : View logs of containers"
	@echo "  make ps      : List running containers"
	@echo "  make help    : Display this help"

.PHONY: init clean up down restart logs ps help