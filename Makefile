clean-data:
	@echo "Cleaning up data..."
	@if docker volume ls | grep -q reporting-tool_datanode-data; then \
		docker volume rm reporting-tool_datanode-data; \
		echo "Removed reporting-tool_datanode-data volume"; \
	fi
	@if docker volume ls | grep -q reporting-tool_namenode-data; then \
		docker volume rm reporting-tool_namenode-data; \
		echo "Removed reporting-tool_namenode-data volume"; \
	fi
	@if docker volume ls | grep -q reporting-tool_mysql-data; then \
		docker volume rm reporting-tool_mysql-data; \
		echo "Removed reporting-tool_mysql-data volume"; \
	fi
	@if docker volume ls | grep -q reporting-tool_tidb-data; then \
		docker volume rm reporting-tool_tidb-data; \
		echo "Removed reporting-tool_tidb-data volume"; \
	fi
	@echo "Data cleaned up successfully!"

clean-images:
	@echo "Cleaning up images..."
	@if docker image ls | grep -q reporting-tool-eureka-discovery-server; then \
		docker image rm reporting-tool-eureka-discovery-server; \
		echo "Removed reporting-tool-eureka-discovery-server image"; \
	fi
	@if docker image ls | grep -q reporting-tool-api-gateway; then \
		docker image rm reporting-tool-api-gateway; \
		echo "Removed reporting-tool-api-gateway image"; \
	fi
	@if docker image ls | grep -q reporting-tool-user-management-service; then \
		docker image rm reporting-tool-user-management-service; \
		echo "Removed reporting-tool-user-management-service image"; \
	fi
	@if docker image ls | grep -q reporting-tool-authentication-service; then \
		docker image rm reporting-tool-authentication-service; \
		echo "Removed reporting-tool-authentication-service image"; \
	fi
	@if docker image ls | grep -q reporting-tool-reporting-service; then \
		docker image rm reporting-tool-reporting-service; \
		echo "Removed reporting-tool-reporting-service image"; \
	fi
	@if docker image ls | grep -q reporting-tool-storage-service; then \
		docker image rm reporting-tool-storage-service; \
		echo "Removed reporting-tool-storage-service image"; \
	fi
	@if docker image ls | grep -q reporting-tool-data-processing-service; then \
		docker image rm reporting-tool-data-processing-service; \
		echo "Removed reporting-tool-data-processing-service image"; \
	fi
	@if docker image ls | grep -q reporting-tool-integrated-service; then \
		docker image rm reporting-tool-integrated-service; \
		echo "Removed reporting-tool-integrated-service image"; \
	fi
	@if docker image ls | grep -q reporting-tool-frontend; then \
		docker image rm reporting-tool-frontend; \
		echo "Removed reporting-tool-frontend image"; \
	fi
	@echo "Images cleaned up successfully!"

# Environment management
env-dev:
	@echo "Switching to development environment..."
	@./scripts/switch-env.sh dev

env-prod:
	@echo "Switching to production environment..."
	@./scripts/switch-env.sh prod

env-current:
	@echo "Showing current environment..."
	@./scripts/switch-env.sh current

env-create-prod:
	@echo "Creating production environment template..."
	@./scripts/switch-env.sh create-prod

env-validate:
	@echo "Validating environment file..."
	@./scripts/switch-env.sh validate

# Clean up
clean:
	@echo "Cleaning up..."
	@sudo make clean-data
	@sudo make clean-images
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
	@echo ""
	@echo "Environment Management:"
	@echo "  make env-dev        : Switch to development environment"
	@echo "  make env-prod       : Switch to production environment"
	@echo "  make env-current    : Show current environment"
	@echo "  make env-create-prod: Create .env.prod template"
	@echo "  make env-validate   : Validate environment file"
	@echo ""
	@echo "Docker Management:"
	@echo "  make up      : Start docker containers"
	@echo "  make down    : Stop docker containers"
	@echo "  make restart : Restart docker containers"
	@echo "  make logs    : View logs of containers"
	@echo "  make ps      : List running containers"
	@echo ""
	@echo "Cleanup:"
	@echo "  make clean   : Clean up data and images"
	@echo "  make help    : Display this help"

.PHONY: env-dev env-prod env-current env-create-prod env-validate clean up down restart logs ps help