# Data Processing Service

A Spring Boot-based microservice for processing and managing data with support for various file formats and distributed processing capabilities.

## Features

- File processing support for multiple formats (CSV, Excel, etc.)
- Integration with Hadoop HDFS for distributed storage
- JWT-based authentication and authorization
- gRPC communication support
- MySQL database integration
- Service discovery with Eureka
- RESTful API endpoints
- Multi-threaded processing capabilities

## Technology Stack

- Java 17
- Spring Boot 3.4.5
- Spring Cloud 2024.0.1
- Spring Security
- Spring Data JPA
- Apache Hadoop 3.4.0
- Apache POI 5.4.0
- gRPC 1.61.0
- MySQL
- Maven
- Docker

## Prerequisites

- Java 17 or higher
- Maven 3.9.8 or higher
- Docker (for containerized deployment)
- MySQL database
- Hadoop HDFS cluster (optional)

## Project Structure

```text
src/main/java/com/haiphamcoder/dataprocessing/
├── config/         # Configuration classes
├── controller/     # REST API controllers
├── domain/         # Domain entities
├── infrastructure/ # Infrastructure components
├── mapper/         # Object mappers
├── repository/     # Data access layer
├── security/       # Security configurations
├── service/        # Business logic
├── shared/         # Shared utilities
└── threads/        # Thread management
```

## Building the Project

```bash
# Clone the repository
git clone <repository-url>

# Navigate to project directory
cd data-processing-service

# Build with Maven
mvn clean install
```

## Running the Service

### Using Maven

```bash
mvn spring-boot:run
```

### Using Docker

```bash
# Build the Docker image
docker build -t data-processing-service .

# Run the container
docker run -p 8080:8080 data-processing-service
```

## Configuration

The service can be configured through application properties. Key configurations include:

- Database connection settings
- Hadoop HDFS configuration
- Security settings
- Service discovery settings
- Thread pool configurations

## API Documentation

The service exposes RESTful APIs for:

- File upload and processing
- Data management
- System monitoring
- Authentication and authorization

## Security

- JWT-based authentication
- Role-based access control
- Secure password handling
- API endpoint protection
