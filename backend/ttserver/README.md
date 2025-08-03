# TomorrowTrader Backend Server

A modern Spring Boot backend application for the TomorrowTrader trading platform, built with Java 21 and Spring Boot 3.5.3.

## 🚀 Quick Start

### Prerequisites
- Java 21 or higher
- Maven 3.8+
- Docker and Docker Compose (for development mode)
- PostgreSQL (for production mode)

### Running the Application

```bash
# Clone the repository and navigate to the backend
cd tomorrowtrader/backend/ttserver

# Run in development mode (default - starts Docker Compose automatically)
./mvnw spring-boot:run

# Run with a specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=test  # H2 in-memory database
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev   # PostgreSQL via Docker
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod  # External PostgreSQL

# Alternative: Using JVM argument
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=test"
```

## 📋 Available Endpoints

Once the server is running (default port 8080), you can access:

### Main Pages
- **Home Page**: http://localhost:8080/
  - Overview of all available services and endpoints
  - Real-time health status monitoring

### API Documentation
- **Swagger UI**: http://localhost:8080/swagger-ui.html
  - Interactive API documentation
  - Test API endpoints directly from the browser
- **OpenAPI Spec**: http://localhost:8080/api-docs
  - Raw OpenAPI JSON specification

### Monitoring & Management
- **Health Check**: http://localhost:8080/actuator/health
  - Application and dependency health status
- **Application Info**: http://localhost:8080/actuator/info
  - Build and version information
- **Metrics**: http://localhost:8080/actuator/metrics
  - Performance metrics and statistics
- **Environment**: http://localhost:8080/actuator/env
  - Configuration properties (dev mode only)
- **Request Mappings**: http://localhost:8080/actuator/mappings
  - All available HTTP endpoints

### API Endpoints
- **API Root**: http://localhost:8080/api/
- **API Health**: http://localhost:8080/api/health

## 🔧 Configuration

### Profiles

The application supports three profiles (see [PROFILES.md](PROFILES.md) for details):

| Profile | Database | Docker Compose | Use Case |
|---------|----------|----------------|----------|
| `test` | H2 In-Memory | Disabled | Unit Tests, CI/CD |
| `dev` | PostgreSQL | Auto-managed | Local Development |
| `prod` | External PostgreSQL | Disabled | Production |

### Environment Variables

For production deployment:
```bash
export DB_URL=jdbc:postgresql://your-db-host:5432/tomorrowtrader
export DB_USERNAME=your-db-user
export DB_PASSWORD=your-db-password
export SPRING_PROFILES_ACTIVE=prod
```

## 🛠️ Development

### Building the Application
```bash
# Clean and compile
./mvnw clean compile

# Run tests (uses H2, no Docker required)
./mvnw test

# Package as JAR
./mvnw package

# Install to local Maven repository
./mvnw install
```

### Docker Compose Services

When running in `dev` profile, Docker Compose automatically starts:
- **PostgreSQL**: Port 5432
  - Database: mydatabase
  - Username: myuser
  - Password: secret
- **Apache Artemis**: Port 61616 (JMS)
  - Web Console: http://localhost:8161
  - Username: artemis
  - Password: artemis

### Database Management

The application uses Liquibase for database schema management:
- Migrations are in `src/main/resources/db/changelog/`
- Automatically applied on startup (except in test profile)
- H2 Console available at http://localhost:8080/h2-console (test profile only)

### Hot Reload

DevTools is enabled in development mode:
- Automatic restart on code changes
- LiveReload for static resources
- Property defaults for development

## 📦 Project Structure

```
ttserver/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/ttserver/
│   │   │       ├── controller/    # REST Controllers
│   │   │       ├── service/       # Business Logic
│   │   │       ├── repository/    # Data Access
│   │   │       └── model/         # Domain Models
│   │   └── resources/
│   │       ├── application.properties       # Base configuration
│   │       ├── application-dev.properties   # Dev profile
│   │       ├── application-test.properties  # Test profile
│   │       ├── application-prod.properties  # Prod profile
│   │       ├── db/changelog/                # Liquibase migrations
│   │       └── static/                      # Static resources
│   └── test/                               # Test sources
├── docker-compose.yaml                      # Local development services
├── pom.xml                                 # Maven configuration
└── README.md                               # This file
```

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=YourTestClass

# Run with coverage
./mvnw test jacoco:report
```

## 🚢 Deployment

### Building for Production
```bash
# Create production JAR
./mvnw clean package -Pprod

# Run the JAR
java -jar target/ttserver-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Docker Deployment
```bash
# Build Docker image
docker build -t tomorrowtrader-backend .

# Run container
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:postgresql://db:5432/tomorrowtrader \
  -e DB_USERNAME=ttuser \
  -e DB_PASSWORD=secret \
  tomorrowtrader-backend
```

## 🔍 Troubleshooting

### Common Issues

1. **Port 8080 already in use**
   ```bash
   # Change port
   ./mvnw spring-boot:run -Dserver.port=8081
   ```

2. **Docker Compose not starting**
   - Ensure Docker Desktop is running
   - Check `docker-compose.yaml` exists in project root
   - Verify you're using the `dev` profile

3. **Database connection issues**
   - Check PostgreSQL is running: `docker ps`
   - Verify credentials in application-dev.properties
   - Check firewall/network settings

4. **Test failures**
   - Ensure you have Java 21: `java -version`
   - Clear Maven cache: `./mvnw clean`
   - Check test profile is using H2

### Logs

```bash
# View application logs
docker-compose logs -f ttserver

# View specific service logs
docker-compose logs -f postgres
docker-compose logs -f artemis
```

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Liquibase Documentation](https://www.liquibase.org/)
- [Docker Compose](https://docs.docker.com/compose/)
- [Apache Artemis](https://activemq.apache.org/components/artemis/)

## 🤝 Contributing

1. Create a feature branch
2. Make your changes
3. Run tests: `./mvnw test`
4. Submit a pull request

## 📄 License

This project is part of the TomorrowTrader application modernization initiative.