# Integrated Service

A Spring Boot microservice that integrates various functionalities including Telegram bot integration and service discovery.

## Overview

This service is part of a microservices architecture and provides integration capabilities with various external services. It's built using Spring Boot 3.4.5 and includes features like service discovery through Eureka, Telegram bot integration, and more.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher
- Docker (optional, for containerized deployment)

## Technology Stack

- Spring Boot 3.4.5
- Spring Cloud 2024.0.1
- Spring Boot Actuator
- Spring Boot Web
- Netflix Eureka Client
- Telegram Bots API 8.2.0
- Lombok
- MySQL 8

## Configuration

The service can be configured through environment variables or application.properties. Key configurations include:

### Server Configuration

- `PORT`: Server port (default: 8080)
- `spring.application.name`: Service name

### Database Configuration

- `MYSQL_SERVER_HOST`: MySQL server host (default: localhost)
- `MYSQL_SERVER_PORT`: MySQL server port (default: 3306)
- `MYSQL_DATABASE`: Database name (default: reporting_tool)
- `MYSQL_USER`: Database username (default: root)
- `MYSQL_PASSWORD`: Database password (default: root)
- `MYSQL_POOL_SIZE`: Connection pool size (default: 5)

### Service Discovery

- `DISCOVERY_SERVER_URL`: Eureka server URL (default: http://localhost:8761)

### Security

- `JWT_SECRET_KEY`: JWT secret key for authentication

### Telegram Bot

- `telegram.bot.token`: Telegram bot token
- `telegram.bot.username`: Telegram bot username

## Building and Running

### Local Development

1. Clone the repository
2. Configure the application.properties or set environment variables
3. Run the application:

```bash
./mvnw spring-boot:run
```

### Docker Deployment

Build and run using Docker:

```bash
docker build -t integrated-service .
docker run -p 8080:8080 integrated-service
```

## API Documentation

The service exposes various endpoints for different functionalities. API documentation will be available at:

- Actuator endpoints: <http://localhost:8080/actuator>
- Swagger UI (if configured): <http://localhost:8080/swagger-ui.html>
