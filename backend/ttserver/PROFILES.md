# Spring Boot Profiles Configuration

This application uses Spring profiles to manage different environments:

## Available Profiles

### `test` Profile
- **Database**: H2 in-memory database
- **Docker Compose**: Disabled
- **Use Case**: Running unit tests, CI/CD pipelines
- **Liquibase**: Disabled (uses JPA ddl-auto=create-drop)
- **Artemis/JMS**: Disabled (excluded from autoconfiguration)
- **H2 Console**: Available at http://localhost:8080/h2-console

### `dev` Profile (Default)
- **Database**: PostgreSQL via Docker Compose
- **Docker Compose**: Enabled (auto-starts/stops with Spring Boot)
- **Use Case**: Local development
- **Liquibase**: Enabled for schema management
- **Features**: DevTools enabled, SQL logging enabled

### `prod` Profile
- **Database**: External PostgreSQL (configured via environment variables)
- **Docker Compose**: Disabled
- **Use Case**: Production deployment
- **Liquibase**: Enabled for schema management
- **Security**: Error details hidden, DevTools disabled

## Usage

### Running with specific profile:
```bash
# Run tests (uses H2)
./mvnw test

# Run in development mode (default, uses Docker Compose)
./mvnw spring-boot:run

# Run with explicit profile
./mvnw spring-boot:run -Dspring.profiles.active=dev

# Run in production mode
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

### Maven commands behavior:
- `mvn compile` - Does NOT start Docker Compose
- `mvn test` - Uses H2, does NOT start Docker Compose
- `mvn install` - Does NOT start Docker Compose
- `mvn spring-boot:run` - Starts Docker Compose (in dev profile only)

### Environment Variables for Production:
```bash
# Required for production
export DB_URL=jdbc:postgresql://prod-server:5432/tomorrowtrader
export DB_USERNAME=produser
export DB_PASSWORD=secure-password
export SPRING_PROFILES_ACTIVE=prod
```

## Docker Compose Behavior

Docker Compose is only started when:
1. The `dev` profile is active
2. The application is run with `spring-boot:run` goal
3. Docker is available on the system

Docker Compose is NOT started during:
- Maven compile/test/install phases
- When running with `test` or `prod` profiles
- When Docker is not available