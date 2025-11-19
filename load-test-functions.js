/**
 * Artillery Load Test Functions
 * Custom functions for generating random calculation requests
 */

const operations = ['add', 'subtract', 'multiply', 'divide'];

/**
 * Generate a random number within a range
 */
function randomNumber(min, max) {
  return Math.random() * (max - min) + min;
}

/**
 * Generate a random integer within a range
 */
function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

/**
 * Generate a random operation
 */
function randomOperation() {
  return operations[Math.floor(Math.random() * operations.length)];
}

/**
 * Generate a random calculation with mixed operations and numbers
 */
function generateRandomCalculation(requestParams, context, ee, next) {
  context.vars.operation = randomOperation();
  context.vars.num1 = randomNumber(1, 1000);
  context.vars.num2 = randomNumber(1, 1000);
  return next();
}

/**
 * Generate an addition operation with cache-friendly numbers
 * Uses smaller range to increase likelihood of cache hits
 */
function generateAddition(requestParams, context, ee, next) {
  // Use integers from 1-50 to increase cache hit probability
  context.vars.num1 = randomInt(1, 50);
  context.vars.num2 = randomInt(1, 50);
  return next();
}

/**
 * Generate a division operation, occasionally with edge cases
 */
function generateDivision(requestParams, context, ee, next) {
  context.vars.num1 = randomNumber(1, 1000);

  // 10% chance of divide by zero to test error handling
  if (Math.random() < 0.1) {
    context.vars.num2 = 0;
  } else {
    // Avoid very small divisors that could cause precision issues
    context.vars.num2 = randomNumber(1, 1000);
  }
  return next();
}

/**
 * Generate calculations with large numbers
 */
function generateLargeNumbers(requestParams, context, ee, next) {
  context.vars.operation = randomOperation();
  context.vars.num1 = randomNumber(1000000, 10000000);
  context.vars.num2 = randomNumber(1000000, 10000000);
  return next();
}

/**
 * Generate calculations with decimal precision
 */
function generateDecimalNumbers(requestParams, context, ee, next) {
  context.vars.operation = randomOperation();
  context.vars.num1 = parseFloat(randomNumber(0.1, 100).toFixed(2));
  context.vars.num2 = parseFloat(randomNumber(0.1, 100).toFixed(2));
  return next();
}

/**
 * Generate calculations with negative numbers
 */
function generateNegativeNumbers(requestParams, context, ee, next) {
  context.vars.operation = randomOperation();
  context.vars.num1 = randomNumber(-1000, 1000);
  context.vars.num2 = randomNumber(-1000, 1000);
  return next();
}

/**
 * After response handler - log slow responses
 */
function afterResponse(requestParams, response, context, ee, next) {
  if (response.timings && response.timings.phases) {
    const totalTime = response.timings.phases.firstByte + response.timings.phases.download;

    // Log slow responses (over 3 seconds)
    if (totalTime > 3000) {
      console.log(`Slow response detected: ${totalTime}ms for ${context.vars.operation} ${context.vars.num1} ${context.vars.num2}`);
    }

    // Log errors
    if (response.statusCode >= 400) {
      console.log(`Error response: ${response.statusCode} for ${context.vars.operation} ${context.vars.num1} ${context.vars.num2}`);
    }
  }
  return next();
}

// Export all functions
module.exports = {
  generateRandomCalculation,
  generateAddition,
  generateDivision,
  generateLargeNumbers,
  generateDecimalNumbers,
  generateNegativeNumbers,
  afterResponse
};
