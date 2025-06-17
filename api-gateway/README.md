# API Gateway Service

## Overview

The API Gateway service is a Spring Cloud Gateway-based service that acts as the entry point for all client requests. It provides routing, load balancing, and other cross-cutting concerns for the microservices architecture.

## Technology Stack

- Java 17
- Spring Boot 3.4.5
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka Client
- Maven

## Features

- API Routing
- Service Discovery Integration with Eureka
- Load Balancing
- Cross-cutting concerns handling
- Actuator endpoints for monitoring

## Prerequisites

- Java 17 or higher
- Maven
- Eureka Discovery Server running (default: <http://localhost:8761>)

## Configuration

The service can be configured through `application.properties`:

```properties
# Default port: 8765
server.port=${PORT:8765}

# Eureka Server URL
eureka.client.serviceUrl.defaultZone=${DISCOVERY_SERVER_URL:http://localhost:8761}/eureka
```

## Building and Running

### Using Maven

```bash
# Build the project
mvn clean package

# Run the application
mvn spring-boot:run
```

### Using Docker

```bash
# Build the Docker image
docker build -t api-gateway .

# Run the container
docker run -p 8765:8765 api-gateway
```

## Environment Variables

- `PORT`: Server port (default: 8765)
- `DISCOVERY_SERVER_URL`: Eureka Server URL (default: <http://localhost:8761>)

## Dependencies

- Spring Cloud Gateway
- Spring Cloud Netflix Eureka Client
- Spring Boot Actuator
- Lombok
- Spring Boot DevTools (for development)

## Development

The project uses Lombok for reducing boilerplate code. Make sure to install the Lombok plugin in your IDE.

## Monitoring

The service exposes actuator endpoints for monitoring. Access them at:

- Health check: `/actuator/health`
- Metrics: `/actuator/metrics`
- Info: `/actuator/info`
