# Stage 1: Builder
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:resolve dependency:resolve-plugins
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
LABEL maintainer="University Microservices"
LABEL description="Notification Service - WebSocket + Kafka"
WORKDIR /app
RUN addgroup -S appuser && adduser -S appuser -G appuser
COPY --from=builder /build/target/notification-service-*.jar application.jar
RUN chown appuser:appuser application.jar
USER appuser
EXPOSE 5000

HEALTHCHECK --interval=10s --timeout=3s --start-period=15s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:5000/health || exit 1

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:InitiatingHeapOccupancyPercent=35 -Dfile.encoding=UTF-8 -Dserver.port=5000"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar application.jar"]
