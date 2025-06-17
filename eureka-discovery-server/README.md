# Eureka Discovery Server

This is a Spring Cloud Netflix Eureka Server implementation that provides service discovery capabilities for microservices architecture.

## Overview

The Eureka Discovery Server is a service registry that allows microservices to register themselves and discover other services. It provides a central registry where services can register their instances and clients can look up service instances.

## Technology Stack

- Java 17
- Spring Boot 3.4.5
- Spring Cloud 2024.0.1
- Netflix Eureka Server

## Prerequisites

- JDK 17 or higher
- Maven 3.6.x or higher

## Configuration

The server runs on port 8761 by default. You can modify the following properties in `application.properties`:

```properties
server.port=${PORT:8761}
eureka.instance.hostname=localhost
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
eureka.client.service-url.default-zone=${DISCOVERY_SERVER_URL:http://localhost:8761}/eureka
```

## Running the Application

### Using Maven

```bash
./mvnw spring-boot:run
```

### Using Docker

Build the Docker image:

```bash
docker build -t eureka-discovery-server .
```

Run the container:

```bash
docker run -p 8761:8761 eureka-discovery-server
```

## Accessing the Eureka Dashboard

Once the application is running, you can access the Eureka Dashboard at:

```text
http://localhost:8761
```

## Features

- Service registration and discovery
- Health monitoring
- Load balancing support
- High availability configuration support

## Dependencies

- Spring Boot Starter Actuator
- Spring Boot Starter Validation
- Spring Cloud Netflix Eureka Server
- Spring Boot DevTools (for development)

## Development

The project uses Spring Boot DevTools for enhanced development experience. It provides features like:

- Automatic application restart
- Live reload
- Remote debugging support
