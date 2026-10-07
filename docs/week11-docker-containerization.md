# Week 11 — Docker Containerisation & Multi-Stage Packaging

## Overview
This document details the Docker containerization strategy, multi-stage [`Dockerfile`](../Dockerfile) design, non-root container security hardening, health check instructions, and Docker Compose orchestration for the CIRT application.

---

## 1. Multi-Stage Dockerfile Specification ([`Dockerfile`](../Dockerfile))

```dockerfile
# Stage 1: Build JAR using Maven & OpenJDK 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

# Stage 2: Runtime image using Eclipse Temurin JRE 21
FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app
RUN apk add --no-cache curl
RUN addgroup -S cirtgroup && adduser -S cirtuser -G cirtgroup
COPY --from=builder /app/target/cirt-devops-1.0.0-SNAPSHOT.jar app.jar
RUN chown -R cirtuser:cirtgroup /app

USER cirtuser

ENV PORT=9090 \
    SPRING_PROFILES_ACTIVE=prod \
    DB_URL=jdbc:postgresql://db:5432/cirt_db \
    DB_USERNAME=postgres \
    DB_PASSWORD=postgres

EXPOSE 9090

HEALTHCHECK --interval=15s --timeout=5s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:9090/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 2. Docker Compose Orchestration ([`docker-compose.yml`](../docker-compose.yml))

```yaml
services:
  db:
    image: postgres:16-alpine
    container_name: cirt-db
    restart: unless-stopped
    environment:
      POSTGRES_DB: cirt_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres -d cirt_db"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 10s

  app:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: cirt-app
    restart: unless-stopped
    ports:
      - "9090:9090"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_URL: jdbc:postgresql://db:5432/cirt_db
      DB_USERNAME: postgres
      DB_PASSWORD: postgres
      SERVER_PORT: "9090"
    depends_on:
      db:
        condition: service_healthy
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:9090/actuator/health || exit 1"]
      interval: 15s
      timeout: 5s
      retries: 5
      start_period: 25s

volumes:
  postgres_data:
    driver: local
```

---

## 3. Essential Docker Lifecycle Commands

### Building Image Manually
```powershell
docker build -t cirt-devops:latest .
```

### Launching Container Stack with Docker Compose
```powershell
docker compose up --build -d
```

### Inspecting Container Health & Logs
```powershell
docker compose ps
docker compose logs -f app
```

### Checking Health Check Status via Docker API
```powershell
docker inspect --format='{{json .State.Health}}' cirt-app
```

### Stopping and Removing Stack
```powershell
docker compose down
```
