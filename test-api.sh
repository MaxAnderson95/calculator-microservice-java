#!/bin/bash
#
# Simple script to test the calculator API manually
# Usage: ./test-api.sh [base_url]
#

BASE_URL="${1:-http://localhost:8000}"
API_ENDPOINT="${BASE_URL}/api/v1/calculate"

echo "Testing Calculator API at: ${API_ENDPOINT}"
echo "========================================"
echo ""

# Test Addition
echo "Test 1: Addition (5 + 3)"
curl -s -X POST "${API_ENDPOINT}" \
  -H "Content-Type: application/json" \
  -d '{"operation":"add","num1":5,"num2":3}' \
  | jq '.'
echo ""

# Test Subtraction
echo "Test 2: Subtraction (10 - 4)"
curl -s -X POST "${API_ENDPOINT}" \
  -H "Content-Type: application/json" \
  -d '{"operation":"subtract","num1":10,"num2":4}' \
  | jq '.'
echo ""

# Test Multiplication
echo "Test 3: Multiplication (7 * 6)"
curl -s -X POST "${API_ENDPOINT}" \
  -H "Content-Type: application/json" \
  -d '{"operation":"multiply","num1":7,"num2":6}' \
  | jq '.'
echo ""

# Test Division
echo "Test 4: Division (20 / 4)"
curl -s -X POST "${API_ENDPOINT}" \
  -H "Content-Type: application/json" \
  -d '{"operation":"divide","num1":20,"num2":4}' \
  | jq '.'
echo ""

# Test Cache (repeat same operation)
echo "Test 5: Cache Test - Same multiplication (7 * 6) - should be faster"
time curl -s -X POST "${API_ENDPOINT}" \
  -H "Content-Type: application/json" \
  -d '{"operation":"multiply","num1":7,"num2":6}' \
  | jq '.'
echo ""

# Test Commutative Cache (for add/multiply)
echo "Test 6: Commutative Cache - Addition (3 + 5) - should hit cache from test 1"
time curl -s -X POST "${API_ENDPOINT}" \
  -H "Content-Type: application/json" \
  -d '{"operation":"add","num1":3,"num2":5}' \
  | jq '.'
echo ""

# Test Error Handling - Divide by Zero
echo "Test 7: Error Handling - Divide by Zero"
curl -s -X POST "${API_ENDPOINT}" \
  -H "Content-Type: application/json" \
  -d '{"operation":"divide","num1":10,"num2":0}' \
  | jq '.'
echo ""

# Test Error Handling - Invalid Operation
echo "Test 8: Error Handling - Invalid Operation"
curl -s -X POST "${API_ENDPOINT}" \
  -H "Content-Type: application/json" \
  -d '{"operation":"power","num1":2,"num2":3}' \
  | jq '.'
echo ""

echo "========================================"
echo "All tests completed!"
