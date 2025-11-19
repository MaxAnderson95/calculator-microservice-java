# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A distributed calculator microservice built with Java 21, Spring Boot 3.4.0, and React. The architecture demonstrates microservice patterns including service-to-service communication, caching, logging, and chaos engineering.

## Build & Test Commands

### Using Make (Recommended)
- `make build` - Build all services (skips tests)
- `make test` - Run tests for all services
- `make clean` - Clean all build artifacts including frontend node_modules
- `make run-prod` - Build and run all services with Docker Compose
- `make stop` - Stop all Docker Compose services

### Using Maven
- `cd services/<service-name> && mvn clean package` - Build a specific service
- `cd services/<service-name> && mvn test` - Run tests for a specific service
- `cd services/<service-name> && mvn spring-boot:run` - Run a service locally

### Frontend Development
- `cd services/frontend/ui && npm install` - Install dependencies
- `cd services/frontend/ui && npm run dev` - Start Vite dev server with hot-reload
- `cd services/frontend/ui && npm run build` - Build production bundle

### Docker Compose
- `docker-compose up --build` - Build and start all services
- `docker-compose down` - Stop and remove containers

## Architecture

### Service Communication Flow
1. **Frontend Service** - API gateway that receives calculation requests, manages caching, and logs to PostgreSQL
2. **Math Services** (add, multiply, divide) - Perform individual operations with artificial 2-second delays
3. **Subtract Service** - Unique pattern: delegates to Add service by negating the second operand
4. **Redis** - Optional caching layer (falls back to in-memory)
5. **PostgreSQL** - Request logging (falls back to H2 in-memory)

### Key Design Patterns

**Interface-Based Caching**
- `CacheService` interface with two implementations:
  - `RedisCacheService` - Production Redis backend
  - `InMemoryCacheService` - Development fallback (default)
- Configurable via `CACHE_TYPE` environment variable (`memory` or `redis`)
- Smart cache key generation normalizes commutative operations (add/multiply)

**Service Delegation**
- Subtract service demonstrates inter-service communication by calling Add service
- Frontend uses `WebClient` (non-blocking) to call all math services
- Error messages propagate through service layers with context attribution

**Chaos Engineering**
- All services include `ChaosInterceptor` that can inject random failures
- Configured via `CHAOS_ENABLED` and `CHAOS_PERCENT` environment variables
- Defaults to disabled; when enabled without percent, uses 5% failure rate

### Package Structure
All services use the same structure under `tech.maxanderson.calculator.{service}`:
- `controller/` - REST endpoints
- `service/` - Business logic
- `dto/` - Request/response objects
- `config/` - Spring configuration (WebClient, Redis, Chaos)
- `exception/` - Custom exceptions and global handlers
- `interceptor/` - HTTP interceptors (chaos engineering)
- `entity/` and `repository/` - JPA (frontend only)

## Important Implementation Details

### Environment Variable Handling
- All services use Spring's `@Value` annotation with defaults
- Docker Compose overrides for production (Redis, PostgreSQL)
- Local development uses fallback implementations (in-memory cache, H2 database)
- Service URLs are configurable: `ADD_SERVICE_URL`, `SUBTRACT_SERVICE_URL`, etc.

### Error Propagation
- Services use custom exceptions: `CalculatorException`, `ServiceException`, `ServiceUnavailableException`
- `GlobalExceptionHandler` in each service provides consistent error responses
- Error messages include service attribution (e.g., "Error from add service: ...")
- WebClient exceptions are caught and transformed into domain exceptions

### Artificial Delays
- Math services include `Thread.sleep(2000)` to demonstrate caching benefits
- Cached results return in <100ms vs ~2 seconds for uncached
- Subtract service has its own 2-second delay PLUS the Add service delay

### Database Schema
The `calculation_log` table (PostgreSQL/H2) stores:
- `operation`, `num1`, `num2`, `result`
- `cache_hit` boolean to track cache effectiveness
- `created_at` timestamp for analytics

### Technologies
- Java 21 features used (pattern matching, records via Lombok)
- Spring Boot 3.4.0 with WebFlux for non-blocking HTTP
- React 18 + TypeScript + Vite + shadcn/ui + Tailwind CSS
- Maven for dependency management (not Gradle)
