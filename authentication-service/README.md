# Authentication Service

## Overview

Authentication Service is a microservice responsible for handling user authentication and authorization in the system. It provides secure user authentication, JWT token management, and integrates with OAuth2 for third-party authentication.

## Technology Stack

- Java 17
- Spring Boot 3.4.5
- Spring Security
- Spring Cloud Netflix Eureka Client
- Spring Data JPA
- MySQL
- gRPC
- JWT (JSON Web Tokens)
- Maven

## Features

- User authentication and authorization
- JWT token generation and validation
- OAuth2 integration
- gRPC service implementation
- Service discovery with Eureka
- Database integration with MySQL
- RESTful API endpoints

## Project Structure

```text
src/main/java/com/haiphamcoder/authentication/
├── config/         # Configuration classes
├── controller/     # REST API controllers
├── domain/         # Domain entities
├── mapper/         # Object mappers
├── repository/     # Data access layer
├── security/       # Security configurations
├── service/        # Business logic
└── shared/         # Shared utilities and constants
```

## Prerequisites

- Java 17 or higher
- Maven
- MySQL
- Docker (optional)

## Getting Started

### Local Development

1. Clone the repository
2. Configure MySQL database connection in `application.properties`
3. Run the application:

   ```bash
   ./mvnw spring-boot:run
   ```

### Docker Deployment

1. Build the Docker image:

   ```bash
   docker build -t authentication-service .
   ```

2. Run the container:

   ```bash
   docker run -p 8080:8080 authentication-service
   ```

## API Documentation

The service exposes REST endpoints for:

- User registration
- User authentication
- Token validation
- User profile management

## Security

- JWT-based authentication
- OAuth2 integration
- Password encryption
- Role-based access control

## Dependencies

- Spring Boot Starter Web
- Spring Boot Starter Security
- Spring Boot Starter OAuth2 Client
- Spring Boot Starter Data JPA
- Spring Cloud Netflix Eureka Client
- MySQL Connector
- gRPC
- JWT libraries

## Configuration

The service can be configured through `application.properties` or environment variables:

- Database connection
- JWT secret key
- OAuth2 client credentials
- Service discovery settings
