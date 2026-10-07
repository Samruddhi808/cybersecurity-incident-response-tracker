# ============================================================
# CIRT DevOps — Multi-stage Dockerfile
# Stage 1: Build application JAR using Maven & Java 21
# Stage 2: Production JRE runtime image
# ============================================================

# --- Stage 1: Build ---
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copy pom.xml and download dependencies (layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and package application
COPY src ./src
RUN mvn package -DskipTests -B

# --- Stage 2: Runtime ---
FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

# Create non-root system user and group for security hardening
RUN addgroup -S cirtgroup && adduser -S cirtuser -G cirtgroup

# Copy built JAR from builder stage
COPY --from=builder /app/target/cirt-devops-1.0.0-SNAPSHOT.jar app.jar

# Set ownership to non-root user
RUN chown -R cirtuser:cirtgroup /app

USER cirtuser

# Default Environment Variables
ENV PORT=9090 \
    SPRING_PROFILES_ACTIVE=prod \
    DB_URL=jdbc:postgresql://db:5432/cirt_db \
    DB_USERNAME=postgres \
    DB_PASSWORD=postgres

EXPOSE 9090

# Health check against Actuator endpoint
HEALTHCHECK --interval=15s --timeout=5s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:9090/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
