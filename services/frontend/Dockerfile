# Frontend build stage
FROM node:20-alpine AS frontend-build
WORKDIR /app/ui

# Copy package files and install dependencies
COPY ui/package*.json ./
RUN npm install

# Copy frontend source and build
COPY ui/ ./
RUN npm run build

# Backend build stage
FROM maven:3.9-eclipse-temurin-21 AS backend-build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy backend source
COPY src ./src

# Copy built frontend from previous stage
COPY --from=frontend-build /app/ui/dist ./src/main/resources/static

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy jar from build stage
COPY --from=backend-build /app/target/*.jar app.jar

# Run the application
EXPOSE 80
ENTRYPOINT ["java", "-jar", "app.jar"]
