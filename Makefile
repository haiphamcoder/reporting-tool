clean-data:
	@echo "Cleaning up data..."
	@docker volume rm reporting-tool_datanode-data
	@docker volume rm reporting-tool_namenode-data
	@docker volume rm reporting-tool_mysql-data
	@docker volume rm reporting-tool_tidb-data
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

# Clean up
clean:
	@echo "Cleaning up..."
	@make clean-data
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