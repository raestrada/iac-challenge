# Demo Spring Boot Application

This is a demonstration application for the Infrastructure as Code project with KubeVela and Tofu-Controller.

## Features

- REST API built with Spring Boot 3.2.4
- Basic `/api/health` endpoint working
- Configured for PostgreSQL connection (initially disabled)
- Actuator for health checks
- Docker ready

## Development Requirements

- JDK 17+
- Maven 3.6+ (or use the included wrapper)

## Local Execution

You can run the application locally using the included Maven wrapper:

```bash
# On Linux/macOS
./mvnw spring-boot:run

# On Windows
mvnw.cmd spring-boot:run
```

## Available Endpoints

- `GET /api/health`: Basic health endpoint
- `GET /actuator/health`: Spring Actuator health check endpoint

## Building with Docker

To build and run the application with Docker:

```bash
# Build the image
docker build -t demo-app .

# Run the container
docker run -p 8080:8080 demo-app
```

## Configuration

The application can be configured using environment variables:

```
SERVER_PORT=8080                  # Server port
SPRING_PROFILES_ACTIVE=default    # Spring profile (default, postgres)
```

## PostgreSQL Connection

By default, the application is configured to use in-memory H2 for development.
The PostgreSQL connection is initially disabled and will be enabled in a later phase.

## Next Steps

- Implement additional endpoints according to the reference PDF
- Enable PostgreSQL integration
- Add unit and integration tests
