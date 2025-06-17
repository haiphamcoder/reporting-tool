# User Management Service

A microservice for managing user accounts, authentication, and authorization built with Spring Boot.

## Technology Stack

- Java 17
- Spring Boot 3.4.5
- Spring Security
- Spring Data JPA
- MySQL
- gRPC
- JWT Authentication
- Eureka Client (Service Discovery)
- Maven

## Project Structure

```text
src/main/java/com/haiphamcoder/usermanagement/
├── config/         # Configuration classes
├── controller/     # REST API controllers
├── domain/         # Domain entities
├── grpc/          # gRPC service implementations
├── mapper/        # Object mappers
├── repository/    # Data access layer
├── security/      # Security configurations and JWT handling
├── service/       # Business logic layer
└── shared/        # Shared utilities and constants
```

## Features

- User registration and management
- JWT-based authentication
- Role-based authorization
- gRPC service endpoints
- Service discovery with Eureka
- Database persistence with MySQL
- RESTful API endpoints

## Prerequisites

- Java 17 or higher
- Maven
- MySQL
- Docker (optional)

## Building the Project

```bash
mvn clean install
```

## Running the Service

### Using Maven

```bash
mvn spring-boot:run
```

### Using Docker

```bash
docker build -t user-management-service .
docker run -p 8080:8080 user-management-service
```

## API Documentation

The service provides both REST and gRPC endpoints for user management operations.

### REST Endpoints

- `POST /api/v1/auth/register` - Register a new user
- `POST /api/v1/auth/login` - Authenticate user and get JWT token
- `GET /api/v1/users` - Get all users (requires authentication)
- `GET /api/v1/users/{id}` - Get user by ID (requires authentication)
- `PUT /api/v1/users/{id}` - Update user (requires authentication)
- `DELETE /api/v1/users/{id}` - Delete user (requires authentication)

### gRPC Services

The service implements gRPC endpoints for user management operations. The proto definitions can be found in the `src/main/proto` directory.

## Security

- JWT-based authentication
- Password encryption
- Role-based access control
- Secure endpoints with Spring Security

## Configuration

The service can be configured through environment variables or application properties:

- Database configuration
- JWT secret key
- Service port
- Eureka server URL
- Other Spring Boot properties
