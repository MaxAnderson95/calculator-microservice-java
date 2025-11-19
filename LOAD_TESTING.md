# Load Testing the Calculator Microservice

This directory contains Artillery load testing scripts for the calculator microservice.

## Prerequisites

Install Artillery:

```bash
npm install -g artillery@latest
```

Or install Artillery plugins if using the main config:

```bash
npm install --save-dev artillery-plugin-expect artillery-plugin-metrics-by-endpoint
```

## Files

- `load-test.yaml` - Main load test configuration with multiple phases
- `load-test-functions.js` - Custom JavaScript functions for generating random test data
- `load-test-quick.yaml` - Quick smoke test (optional)
- `load-test-soak.yaml` - Long-running soak test (optional)

## Running the Tests

### Start the Application

First, start the calculator microservice:

```bash
# Using Docker Compose
make run-prod

# Or using Kubernetes/Helm
helm install calculator ./charts/calculator-microservice-java
kubectl port-forward svc/calculator-frontend 8000:80
```

### Run the Main Load Test

Run the full load test with warm-up, ramp-up, peak load, and ramp-down:

```bash
artillery run load-test.yaml
```

### Run with Custom Target

Test against a different environment:

```bash
artillery run --target http://calculator.example.com load-test.yaml
```

### Generate HTML Report

Run the test and generate a detailed HTML report:

```bash
artillery run --output report.json load-test.yaml
artillery report report.json
```

This will create `report.json.html` which you can open in a browser.

## Test Phases

The main load test includes 7 phases:

1. **Warm-up (60s)** - 5 requests/sec
   - Warms up JVM, caches, and database connections
   - Ensures services are fully initialized

2. **Ramp-up (120s)** - 10 → 50 requests/sec
   - Gradually increases load
   - Identifies performance degradation points

3. **Peak Load (180s)** - 50 requests/sec
   - Sustained high load
   - Tests system stability under continuous load

4. **Spike (30s)** - 100 requests/sec
   - Short burst of very high load
   - Tests system resilience to traffic spikes

5. **Recovery (60s)** - 50 requests/sec
   - Back to sustained load
   - Verifies system recovers from spike

6. **Ramp-down (120s)** - 50 → 10 requests/sec
   - Gradually decreases load
   - Observes system behavior during load decrease

7. **Cool-down (60s)** - 5 requests/sec
   - Low load to observe recovery
   - Final stability check

**Total Duration:** ~11 minutes

## Test Scenarios

The load test includes multiple scenarios with different weights:

### 1. Random Calculations (70% of traffic)
- Mixed operations (add, subtract, multiply, divide)
- Random numbers between 1-1000
- Tests overall system behavior

### 2. Addition Heavy (10% of traffic)
- Focuses on addition operations
- Uses numbers 1-50 to increase cache hit probability
- Tests cache effectiveness (add is commutative)

### 3. Division Tests (5% of traffic)
- Tests division operation
- Includes 10% divide-by-zero for error handling
- Validates error responses

### 4. Large Number Calculations (5% of traffic)
- Numbers between 1,000,000 - 10,000,000
- Tests numerical precision and performance

### 5. Cache Hit Test (10% of traffic)
- Repeats the same calculation 5 times
- Tests cache hit ratio
- Should see <100ms response after first request

## Performance Thresholds

The test includes automatic validation:

- **P95 Response Time:** < 3000ms
- **P99 Response Time:** < 5000ms
- **Max Response Time:** < 10000ms

Note: Thresholds account for the 2-second artificial delay in math services.

## Interpreting Results

### Key Metrics to Monitor

1. **Response Times**
   - First request: ~2000ms (uncached)
   - Cached requests: <100ms
   - Watch for degradation over time

2. **Error Rate**
   - Should be <1% (excluding intentional divide-by-zero)
   - Higher rates indicate system stress

3. **Requests Per Second**
   - Verify actual RPS matches configured arrival rate
   - Drops indicate system saturation

4. **Cache Hit Ratio**
   - Check application logs for cache effectiveness
   - Addition and multiplication should have higher hit rates

### Expected Behavior

**Normal Operation:**
- P95 response time: 2000-2500ms
- P99 response time: 2500-3000ms
- Error rate: <1%
- Successful requests: >99%

**Cache Effectiveness:**
- First request of unique calculation: ~2000ms
- Repeated calculations: <100ms
- Addition/multiplication with same numbers (different order): <100ms

**Under Stress:**
- Response times may increase during spike phase
- System should recover during recovery phase
- No cascading failures

## Advanced Usage

### Test with Chaos Engineering Enabled

```bash
# Enable chaos in docker-compose or Kubernetes
CHAOS_ENABLED=true CHAOS_PERCENT=10 make run-prod

# Run load test to observe resilience
artillery run load-test.yaml
```

### Monitor During Test

**View logs:**
```bash
# Docker Compose
docker-compose logs -f frontend

# Kubernetes
kubectl logs -f -l app.kubernetes.io/component=frontend
```

**Watch metrics:**
```bash
# Kubernetes
kubectl top pods
watch -n 1 'kubectl get pods'
```

### Test Different Cache Backends

**In-memory cache:**
```bash
# Default configuration
artillery run load-test.yaml
```

**Redis cache:**
```bash
# With Redis enabled
CACHE_TYPE=redis make run-prod
artillery run load-test.yaml
```

Compare cache hit rates and response times between the two.

## Quick Smoke Test

For a quick validation (if you create load-test-quick.yaml):

```bash
artillery quick --count 100 --num 10 http://localhost:8000/api/v1/calculate
```

## Continuous Load Testing

For CI/CD integration:

```bash
# Run test and fail if thresholds are breached
artillery run --quiet load-test.yaml || echo "Load test failed!"

# Set exit code based on results
artillery run load-test.yaml
if [ $? -ne 0 ]; then
  echo "Performance degradation detected"
  exit 1
fi
```

## Troubleshooting

### High Error Rates

- Check if services are running: `docker-compose ps` or `kubectl get pods`
- Verify network connectivity
- Check service logs for errors

### Response Times Higher Than Expected

- Ensure services are warmed up (run warm-up phase first)
- Check if chaos engineering is enabled unintentionally
- Verify database/cache are running
- Check system resources (CPU, memory)

### Artillery Connection Errors

- Verify target URL is correct
- Check if port forwarding is active (Kubernetes)
- Ensure no firewall blocking requests
- Try `curl http://localhost:8000/api/v1/calculate -X POST -H "Content-Type: application/json" -d '{"operation":"add","num1":5,"num2":3}'`

## Example Results

Successful test output should look like:

```
Summary report @ 15:30:45(+0000)
  Scenarios launched:  12345
  Scenarios completed: 12345
  Requests completed:  12345
  Mean response time:  2100 ms
  Response time (P95): 2450 ms
  Response time (P99): 2850 ms
  Successful requests: 12223
  Failed requests:     122 (0.99%)
```

## Next Steps

1. **Baseline Testing** - Run tests without chaos to establish baseline metrics
2. **Chaos Testing** - Enable chaos engineering and verify graceful degradation
3. **Scaling Tests** - Increase replica counts and retest
4. **Soak Testing** - Run extended tests (hours) to find memory leaks
5. **Stress Testing** - Push beyond limits to find breaking points
