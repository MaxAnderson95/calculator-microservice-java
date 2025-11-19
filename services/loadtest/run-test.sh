#!/bin/sh
set -e

echo "========================================="
echo "Calculator Microservice Load Test"
echo "========================================="

# Check if load testing is enabled
if [ "${LOADTEST_ENABLED}" != "true" ]; then
  echo "Load testing is DISABLED"
  echo "Set LOADTEST_ENABLED=true to enable continuous load testing"
  echo "========================================="
  exit 0
fi

echo "Load testing is ENABLED"
echo "Continuous Load Testing Mode"
echo "Target URL: ${TARGET_URL}"
echo "========================================="

# Wait for the frontend service to be healthy
echo "Waiting for frontend service to be ready..."
MAX_RETRIES=60
RETRY_COUNT=0

until wget -q --spider "${TARGET_URL}/actuator/health" || [ $RETRY_COUNT -eq $MAX_RETRIES ]; do
  RETRY_COUNT=$((RETRY_COUNT+1))
  echo "Waiting for frontend... (${RETRY_COUNT}/${MAX_RETRIES})"
  sleep 2
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
  echo "ERROR: Frontend service did not become healthy in time"
  exit 1
fi

echo "Frontend service is ready!"
echo ""

# Update target URL in test file
echo "Configuring test with target URL: ${TARGET_URL}"
sed -i "s|target: \".*\"|target: \"${TARGET_URL}\"|g" load-test.yaml

echo ""
echo "========================================="
echo "Starting continuous load test..."
echo "Press Ctrl+C to stop"
echo "========================================="
echo ""

# Run the test in an endless loop
ITERATION=1
while true; do
  echo ""
  echo "========================================="
  echo "Load Test Iteration #${ITERATION}"
  echo "Started at: $(date)"
  echo "========================================="
  echo ""

  npx artillery run load-test.yaml

  echo ""
  echo "========================================="
  echo "Iteration #${ITERATION} completed at: $(date)"
  echo "Pausing 30 seconds before next iteration..."
  echo "========================================="

  sleep 30

  ITERATION=$((ITERATION+1))
done
