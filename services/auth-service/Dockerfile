# Stage 1: Builder
# Use official Maven image with JDK 21 for building
FROM maven:3.9-eclipse-temurin-21 AS builder

# Set working directory
WORKDIR /build

# Copy POM file first for better layer caching
# Dependencies layer will only rebuild if pom.xml changes
COPY pom.xml .

# Download dependencies (this layer will be cached)
RUN mvn dependency:resolve dependency:resolve-plugins

# Copy source code
COPY src ./src

# Build the application (skip tests for faster builds in CI/CD)
RUN mvn clean package -DskipTests

# Stage 2: Runtime
# Use minimal Alpine-based JRE image
FROM eclipse-temurin:21-jre-alpine

# Set metadata labels
LABEL maintainer="University Microservices"
LABEL description="Auth Service - Spring Boot microservice"

# Set working directory
WORKDIR /app

# Create non-root user for security
RUN addgroup -S appuser && adduser -S appuser -G appuser

# Copy only the built JAR from builder stage
COPY --from=builder /build/target/auth-service-*.jar application.jar

# Change ownership to non-root user
RUN chown appuser:appuser application.jar

# Switch to non-root user
USER appuser

# Expose port 5000
EXPOSE 5000

# Health check - ensures container is considered healthy
# Checks the /health endpoint every 10 seconds with a 3-second timeout
HEALTHCHECK --interval=10s --timeout=3s --start-period=15s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:5000/health || exit 1

# Set JVM options for optimal container performance
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:InitiatingHeapOccupancyPercent=35 -Dfile.encoding=UTF-8 -Dserver.port=5000"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar application.jar"]

