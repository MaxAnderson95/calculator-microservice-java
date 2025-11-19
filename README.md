# Calculator Microservice (Java)

A distributed calculator service built with Java 25, Spring Boot, and React.

## Services

| Service | Purpose | Notes |
|---------|---------|-------|
| frontend | React UI + Spring Boot API gateway | Modern React UI built with Vite and shadcn/ui. Calls other services for calculations. |
| add | Addition service | Simple addition operation with 2-second artificial delay. |
| subtract | Subtraction service | Delegates to add service by negating the second operand. |
| multiply | Multiplication service | Simple multiplication operation with 2-second artificial delay. |
| divide | Division service | Division with zero-division error handling. |
| redis | Caching | Caches computed calculations. Each math service has a 2-second delay to demonstrate caching benefits. |
| postgres | Request logging | Logs all successful calculation requests using Spring Data JPA. |

## Architecture

### Technology Stack

- **Backend**: Java 25, Spring Boot 3.4.0, Maven
- **Frontend**: React 18, TypeScript, Vite, shadcn/ui, Tailwind CSS
- **Caching**: Redis (with in-memory fallback)
- **Database**: PostgreSQL (with H2 in-memory fallback)
- **Containerization**: Docker, Docker Compose

### Key Features

**Abstracted Caching Layer**
- Interface-based design with two implementations:
  - `RedisCacheService`: Redis-backed cache for production
  - `InMemoryCacheService`: In-memory cache for development (default)
- Configurable via `CACHE_TYPE` environment variable
- Smart cache keys for commutative operations (add, multiply)

**Flexible Database Support**
- PostgreSQL for production
- H2 in-memory database for local development (default)
- Configurable via environment variables

**Modern React Frontend**
- Built with Vite for fast development
- shadcn/ui components for beautiful, accessible UI
- Tailwind CSS for styling
- Loading states and error handling
- Gradient backgrounds and smooth animations

**Service Architecture**
- Each service is independently deployable
- REST API communication between services
- WebClient for non-blocking HTTP calls
- Comprehensive error handling

## Environment Variables

### Frontend Service

| Variable | Description | Default |
|----------|-------------|---------|
| `PORT` | Server port | `8080` |
| `ADD_SERVICE_URL` | Add service URL | `http://localhost:8001` |
| `SUBTRACT_SERVICE_URL` | Subtract service URL | `http://localhost:8002` |
| `MULTIPLY_SERVICE_URL` | Multiply service URL | `http://localhost:8003` |
| `DIVIDE_SERVICE_URL` | Divide service URL | `http://localhost:8004` |
| `CACHE_TYPE` | Cache implementation (`memory` or `redis`) | `memory` |
| `REDIS_HOST` | Redis hostname | `localhost` |
| `REDIS_PORT` | Redis port | `6379` |
| `DB_URL` | Database JDBC URL | `jdbc:h2:mem:calculatordb` |
| `DB_USER` | Database username | `sa` |
| `DB_PASSWORD` | Database password | `` |
| `DB_DRIVER` | Database driver class | `org.h2.Driver` |

### Math Services (add, multiply, divide)

| Variable | Description | Default |
|----------|-------------|---------|
| `PORT` | Server port | `8080` |

### Subtract Service

| Variable | Description | Default |
|----------|-------------|---------|
| `PORT` | Server port | `8080` |
| `ADD_SERVICE_URL` | Add service URL | `http://localhost:8001` |

## Running Locally

### With Docker Compose (Recommended)

```bash
docker-compose up --build
```

Access the application:
- **Frontend**: http://localhost:8000
- **Add Service**: http://localhost:8001
- **Subtract Service**: http://localhost:8002
- **Multiply Service**: http://localhost:8003
- **Divide Service**: http://localhost:8004

### Running Individual Services

Each service can be run independently using Maven:

```bash
# Add service
cd services/add
mvn spring-boot:run

# Subtract service
cd services/subtract
mvn spring-boot:run -Dspring-boot.run.arguments="--add.service.url=http://localhost:8001"

# Multiply service
cd services/multiply
mvn spring-boot:run

# Divide service
cd services/divide
mvn spring-boot:run

# Frontend service (requires Node.js for UI build)
cd services/frontend/ui
npm install
npm run build
cd ..
mvn spring-boot:run
```

## Development

### Frontend Development

The React frontend can be developed with hot-reload:

```bash
cd services/frontend/ui
npm install
npm run dev
```

This will start the Vite dev server with proxy configuration to the Spring Boot backend.

### Building

```bash
# Build all services
for service in add subtract multiply divide frontend; do
  cd services/$service
  mvn clean package
  cd ../..
done
```

### Testing

```bash
# Run tests for a specific service
cd services/add
mvn test

# Run tests for all services
for service in add subtract multiply divide frontend; do
  cd services/$service
  mvn test
  cd ../..
done
```

## API Documentation

### Calculate Endpoint

**POST** `/api/v1/calculate`

Request body:
```json
{
  "operation": "add|subtract|multiply|divide",
  "num1": 10.5,
  "num2": 5.2
}
```

Response:
```json
42.5
```

Error response:
```json
{
  "detail": "Cannot divide by zero"
}
```

### Individual Service Endpoints

Each math service exposes its own endpoint:

- **POST** `/api/v1/add` - Addition
- **POST** `/api/v1/subtract` - Subtraction
- **POST** `/api/v1/multiply` - Multiplication
- **POST** `/api/v1/divide` - Division

Request body format:
```json
{
  "num1": 10,
  "num2": 5
}
```

Response format:
```json
{
  "result": 15
}
```

## Package Structure

All services follow the package naming convention: `tech.maxanderson.calculator.{service-name}`

```
tech.maxanderson.calculator.{service}
├── controller/          # REST controllers
├── service/            # Business logic
├── dto/                # Data transfer objects
├── entity/             # JPA entities (frontend only)
├── repository/         # Data repositories (frontend only)
├── config/             # Configuration classes
└── exception/          # Custom exceptions
```

## Caching Behavior

- Cached operations return results in <100ms
- Non-cached operations take ~2 seconds (artificial delay)
- Commutative operations (add, multiply) normalize operand order for cache keys
- Cache can be switched between Redis and in-memory without code changes

## Database Schema

### calculation_log table

| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key |
| operation | VARCHAR | Operation type (add, subtract, multiply, divide) |
| num1 | DOUBLE | First operand |
| num2 | DOUBLE | Second operand |
| result | DOUBLE | Calculation result |
| cache_hit | BOOLEAN | Whether result came from cache |
| created_at | TIMESTAMP | Log entry creation time |

## License

MIT
