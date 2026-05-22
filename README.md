# Booking System API

Spring Boot backend application for a comprehensive booking system supporting accommodations, bookings, reviews, and payments.

## Features

- **User Management**: Registration, authentication, and authorization with JWT tokens
- **Accommodation Management**: Create, update, and manage accommodation listings
- **Booking System**: Create and manage bookings with availability checking
- **Reviews & Ratings**: Leave reviews and ratings for accommodations
- **Payment Processing**: Manage payment information and booking payments
- **File Management**: Upload and download documents
- **Advanced Search**: Pagination, sorting, filtering, and search capabilities
- **API Documentation**: Swagger UI for comprehensive API docs
- **Security**: Spring Security with JWT authentication
- **Logging**: Comprehensive logging for requests and errors
- **Docker Support**: Containerized deployment with Docker and Docker Compose

## Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 12+
- Docker and Docker Compose (for containerized deployment)

## Getting Started

### Running Locally

1. Clone the repository
2. Configure PostgreSQL connection in `application.yml`
3. Run: `mvn clean install`
4. Start: `mvn spring-boot:run`

### Running with Docker

```bash
docker-compose up
```

## API Documentation

Once the application is running, visit:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

## Project Structure

```
src/main/java/com/booking/
├── config/          - Configuration classes
├── controller/      - REST endpoints
├── dto/            - Data Transfer Objects
├── entity/         - JPA entities
├── exception/      - Custom exceptions
├── listener/       - Event listeners
├── mapper/         - DTO mappers
├── repository/     - Data access layer
├── security/       - Security configuration
├── service/        - Business logic
└── util/           - Utility classes
```

## License

MIT License