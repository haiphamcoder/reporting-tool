# Reporting Service

A Spring Boot-based microservice for handling reporting functionalities, integrated with Hadoop HDFS for data storage and gRPC for service communication.

## Technology Stack

- Java 17
- Spring Boot 3.4.5
- Spring Cloud 2024.0.1
- Spring Security with JWT
- Spring Data JPA
- MySQL
- Apache Hadoop 3.4.0
- gRPC 1.61.0
- Maven
- Docker

## Prerequisites

- Java 17 or higher
- Maven 3.9.8 or higher
- Docker (for containerized deployment)
- MySQL database
- Apache Hadoop cluster

## Project Structure

```text
reporting-service/
├── src/                    # Source code
├── config/                 # Configuration files
├── bin/                    # Shell scripts
├── target/                 # Build output
├── .mvn/                   # Maven wrapper
├── pom.xml                 # Maven configuration
├── Dockerfile             # Docker configuration
└── README.md              # Project documentation
```

## Features

- RESTful API endpoints for reporting operations
- JWT-based authentication and authorization
- Integration with Hadoop HDFS for data storage
- gRPC service communication
- Service discovery with Eureka
- Actuator endpoints for monitoring
- Docker containerization

## Building the Project

### Local Development

1. Clone the repository
2. Configure your MySQL database settings in `application.properties`
3. Run the following commands:

```bash
mvn clean install
mvn spring-boot:run
```

### Docker Build

```bash
docker build -t reporting-service .
```

## Running the Service

### Local Run

```bash
./mvnw spring-boot:run
```

### Docker Run

```bash
docker run -p 8080:8080 reporting-service
```

## Configuration

The service can be configured through environment variables or application properties:

- Database configuration
- Hadoop HDFS settings
- JWT security settings
- Service discovery settings

## API Documentation

The service exposes RESTful APIs for reporting operations. API documentation will be available at:

```text
http://localhost:8080/swagger-ui.html
```

## Security

The service implements JWT-based authentication. Include the JWT token in the Authorization header for protected endpoints:

```text
Authorization: Bearer <your-jwt-token>
```

## Monitoring

The service exposes actuator endpoints for monitoring:

```text
http://localhost:8080/actuator
```
