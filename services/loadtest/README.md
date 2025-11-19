# Load Test Service

Continuous load testing service for the calculator microservice using Artillery.

## Overview

This service runs performance tests continuously in a loop against the calculator API. Each test cycle includes warm-up, ramp-up, peak load, spike testing, and cool-down phases.

## Configuration

Configure via environment variables:

- `TARGET_URL` - Base URL of the service to test (default: `http://frontend:80`)

## Test Phases

Each 11-minute test iteration includes:

1. **Warm-up** (60s) - 5 requests/sec to warm caches and JVM
2. **Ramp-up** (120s) - Gradual increase from 10 to 50 requests/sec
3. **Peak Load** (180s) - Sustained 50 requests/sec
4. **Spike** (30s) - Burst of 100 requests/sec
5. **Recovery** (60s) - Back to 50 requests/sec
6. **Ramp-down** (120s) - Gradual decrease from 50 to 10 requests/sec
7. **Cool-down** (60s) - 5 requests/sec to observe recovery

After each iteration, the test pauses for 30 seconds before starting the next cycle.

## Test Scenarios

- **Random Calculations** (70%) - Mixed operations with random numbers
- **Addition Heavy** (10%) - Tests cache effectiveness
- **Division Tests** (5%) - Includes edge cases like divide-by-zero
- **Large Numbers** (5%) - Tests with large numeric values
- **Cache Hit Test** (10%) - Repeated calculations to test caching

## Docker Compose

```bash
# Enable load testing
LOADTEST_ENABLED=true docker-compose up

# Disable load testing (default)
docker-compose up
```

## Files

- `load-test.yaml` - Artillery test configuration
- `load-test-functions.js` - Random data generation functions
- `run-test.sh` - Continuous loop wrapper script
