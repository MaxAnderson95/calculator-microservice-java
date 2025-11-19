FROM node:22-alpine

WORKDIR /loadtest

# Copy package files and install dependencies
COPY package.json ./
RUN npm install

# Copy test configuration files
COPY load-test.yaml ./
COPY load-test-functions.js ./
COPY run-test.sh ./

# Make the wrapper script executable
RUN chmod +x run-test.sh

# Environment variables with defaults
ENV TARGET_URL=http://frontend:80

# Run the continuous test loop
ENTRYPOINT ["./run-test.sh"]
