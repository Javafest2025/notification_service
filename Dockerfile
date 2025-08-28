# Notification Service Dockerfile
FROM openjdk:21-jdk-slim

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy Maven files for dependency caching
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Make mvnw executable
RUN chmod +x ./mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip Spotless formatting check)
RUN ./mvnw clean package -DskipTests -Dspotless.check.skip=true

# Create non-root user
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Expose port
EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8082/actuator/health || exit 1

# Environment variables
ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_PORT=8082

# Run the application
ENTRYPOINT ["java", "-jar", "target/notification_service-0.0.1-SNAPSHOT.jar"]
