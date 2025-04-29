# Reporting Tool

A reporting tool that provides comprehensive reporting capabilities, user management, and social media data integration. This service is built using Spring Boot and follows a layered architecture pattern.

## Features

- **Reporting & Analytics**
  - User activity reports
  - Social media engagement metrics
  - Custom report generation
  - Data export capabilities
  - Real-time analytics dashboard

- **Authentication & Authorization**
  - JWT-based authentication
  - Role-based access control
  - Token management (access & refresh tokens)
  - Secure logout functionality

- **User Management**
  - User registration and profile management
  - Admin user management capabilities
  - Role-based permissions

- **Social Media Integration**
  - Facebook data integration for reporting
  - Instagram API integration for analytics
  - Social media data synchronization
  - Engagement metrics tracking

## Architecture

```mermaid
graph TB
    User((External User))
    Admin((Admin User))

    subgraph "CDP Service System"
        subgraph "API Layer"
            APIGateway["API Gateway<br>Spring Web"]
            
            subgraph "Controllers"
                AuthController["Authentication Controller<br>Spring REST"]
                AdminController["Admin Controller<br>Spring REST"]
                UserController["User Controller<br>Spring REST"]
                IndexController["Index Controller<br>Spring REST"]
            end
        end

        subgraph "Security Layer"
            SecurityConfig["Security Configuration<br>Spring Security"]
            JWTFilter["JWT Authentication Filter<br>Spring Security"]
            JWTProvider["JWT Token Provider<br>JJWT"]
            AuthEntryPoint["Authentication Entry Point<br>Spring Security"]
        end

        subgraph "Service Layer"
            AuthService["Authentication Service<br>Spring Service"]
            UserService["User Service<br>Spring Service"]
            TokenServices["Token Services<br>Spring Service"]
            LogoutService["Logout Service<br>Spring Service"]
        end

        subgraph "Repository Layer"
            UserRepo["User Repository<br>Spring Data JPA"]
            AccessTokenRepo["Access Token Repository<br>Spring Data JPA"]
            RefreshTokenRepo["Refresh Token Repository<br>Spring Data JPA"]
        end

        subgraph "Data Layer"
            Database[("MySQL Database<br>MySQL")]
        end

        subgraph "External Integrations"
            FacebookAPI["Facebook Integration<br>Facebook SDK"]
            InstagramAPI["Instagram Integration<br>Instagram API"]
        end
    end

    %% User interactions
    User -->|"Authenticates"| APIGateway
    Admin -->|"Manages"| APIGateway

    %% API Gateway connections
    APIGateway -->|"Routes"| AuthController
    APIGateway -->|"Routes"| AdminController
    APIGateway -->|"Routes"| UserController
    APIGateway -->|"Routes"| IndexController

    %% Security flow
    AuthController -->|"Uses"| SecurityConfig
    SecurityConfig -->|"Configures"| JWTFilter
    JWTFilter -->|"Uses"| JWTProvider
    SecurityConfig -->|"Uses"| AuthEntryPoint

    %% Service layer connections
    AuthController -->|"Uses"| AuthService
    UserController -->|"Uses"| UserService
    AuthService -->|"Uses"| TokenServices
    AuthService -->|"Uses"| UserService
    AuthController -->|"Uses"| LogoutService

    %% Repository connections
    UserService -->|"Uses"| UserRepo
    TokenServices -->|"Uses"| AccessTokenRepo
    TokenServices -->|"Uses"| RefreshTokenRepo

    %% Database connections
    UserRepo -->|"Persists"| Database
    AccessTokenRepo -->|"Persists"| Database
    RefreshTokenRepo -->|"Persists"| Database

    %% External integrations
    AuthService -->|"Integrates"| FacebookAPI
    AuthService -->|"Integrates"| InstagramAPI
```

## Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.8 or higher
- Facebook Developer Account (for Facebook integration)
- Instagram Business Account (for Instagram integration)

## Setup

1. **Clone the repository**

2. **Configure the database**

3. **Build the project**

4. **Run the application**

## API Documentation

The API documentation is available at `http://localhost:8080/swagger-ui.html` when the application is running.

### Key Endpoints

- **Reporting & Analytics**
  - GET `/api/reports/user-activity` - Get user activity reports
  - GET `/api/reports/social-engagement` - Get social media engagement metrics
  - POST `/api/reports/generate` - Generate custom reports
  - GET `/api/reports/export/{format}` - Export reports in specified format (CSV, PDF, Excel)
  - GET `/api/reports/dashboard` - Get real-time analytics dashboard data

- **Authentication**
  - POST `/api/auth/login` - User login
  - POST `/api/auth/register` - User registration
  - POST `/api/auth/refresh` - Refresh access token
  - POST `/api/auth/logout` - User logout

- **User Management**
  - GET `/api/users/profile` - Get user profile
  - PUT `/api/users/profile` - Update user profile
  - GET `/api/admin/users` - List all users (Admin only)
  - PUT `/api/admin/users/{id}` - Update user (Admin only)

- **Social Media Integration**
  - GET `/api/social/facebook/metrics` - Get Facebook engagement metrics
  - GET `/api/social/instagram/metrics` - Get Instagram engagement metrics
  - GET `/api/social/facebook/analytics` - Get Facebook analytics data
  - GET `/api/social/instagram/analytics` - Get Instagram analytics data

## Security

- JWT-based authentication with configurable token expiration
- Password encryption using BCrypt
- CSRF protection
- Rate limiting on authentication endpoints
- Secure session management

## Development

### Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── cdp/
│   │           ├── config/        # Configuration classes
│   │           ├── controller/    # REST controllers
│   │           ├── model/         # Data models
│   │           ├── repository/    # JPA repositories
│   │           ├── security/      # Security configuration
│   │           ├── service/       # Business logic
│   │           └── util/          # Utility classes
│   └── resources/
│       ├── application.properties # Application configuration
│       └── application.yml        # Additional configuration
```

## Contributing

## License

## Support
