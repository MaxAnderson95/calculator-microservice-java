.PHONY: build test clean run-dev run-prod stop load-test-install load-test load-test-quick load-test-soak

# Build all services
build:
	@echo "Building all services..."
	@for service in add subtract multiply divide frontend; do \
		echo "Building $$service service..."; \
		cd services/$$service && mvn clean package -DskipTests && cd ../..; \
	done

# Run tests for all services
test:
	@echo "Running tests for all services..."
	@for service in add subtract multiply divide frontend; do \
		echo "Testing $$service service..."; \
		cd services/$$service && mvn test && cd ../..; \
	done

# Clean all build artifacts
clean:
	@echo "Cleaning all services..."
	@for service in add subtract multiply divide frontend; do \
		echo "Cleaning $$service service..."; \
		cd services/$$service && mvn clean && cd ../..; \
	done
	@rm -rf services/frontend/ui/node_modules
	@rm -rf services/frontend/ui/dist

# Build and run with Docker Compose
run-prod:
	docker-compose up --build

# Run services locally (requires manual startup of each service)
run-dev:
	@echo "To run services locally:"
	@echo "1. Start Redis: docker run -d -p 6379:6379 redis:alpine"
	@echo "2. Start Postgres: docker run -d -p 5432:5432 -e POSTGRES_PASSWORD=postgres postgres:16-alpine"
	@echo "3. Run each service with: cd services/<service> && mvn spring-boot:run"

# Stop Docker Compose services
stop:
	docker-compose down

# Install frontend dependencies
install-ui:
	cd services/frontend/ui && npm install

# Build frontend for production
build-ui:
	cd services/frontend/ui && npm run build

# Run frontend in development mode
dev-ui:
	cd services/frontend/ui && npm run dev

# Install load testing dependencies
load-test-install:
	@echo "Installing Artillery and plugins..."
	npm install

# Run main load test
load-test:
	@echo "Running main load test..."
	npm run test:load:report

# Run quick smoke test
load-test-quick:
	@echo "Running quick load test..."
	npm run test:quick:report

# Run soak test (1 hour)
load-test-soak:
	@echo "Running soak test (this will take ~1 hour)..."
	npm run test:soak:report

# Clean load test reports
load-test-clean:
	npm run clean
