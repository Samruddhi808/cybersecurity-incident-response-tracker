# Week 5 — Database & Application Setup

## Overview
This document details the persistent data architecture, database schema, configuration externalization, Spring Data JPA mappings, and execution profiles for the CIRT application.

---

## 1. Database Architecture & Schema Design

CIRT utilizes a relational database structure designed around the `incidents` table. Spring Data JPA automatically manages schema creation and table synchronization.

### Entity Model: `com.cirt.model.Incident`

| Column Name | SQL Data Type | Constraint | Java Field | Description |
|---|---|---|---|---|
| `id` | `BIGINT` | `PRIMARY KEY`, Auto Increment | `Long id` | Unique incident identifier |
| `title` | `VARCHAR(255)` | `NOT NULL` | `String title` | Concise summary of the incident |
| `description` | `TEXT` | `NOT NULL` | `String description` | Detailed incident event narrative |
| `category` | `VARCHAR(50)` | `NOT NULL` | `Category category` | Enum: `PHISHING`, `MALWARE`, `UNAUTHORIZED_ACCESS`, `DOS`, `DATA_LEAK`, `OTHER` |
| `severity` | `VARCHAR(50)` | `NOT NULL` | `Severity severity` | Enum: `LOW`, `MEDIUM`, `HIGH`, `CRITICAL` |
| `status` | `VARCHAR(50)` | `NOT NULL` | `Status status` | Enum: `OPEN`, `INVESTIGATING`, `CONTAINED`, `RESOLVED`, `CLOSED` |
| `reported_by` | `VARCHAR(255)` | `NOT NULL` | `String reportedBy` | Name or email of incident reporter |
| `assigned_to` | `VARCHAR(255)` | Optional | `String assignedTo` | Name or team assigned for resolution |
| `created_at` | `TIMESTAMP` | `NOT NULL` | `LocalDateTime createdAt` | Auto-populated creation timestamp |
| `updated_at` | `TIMESTAMP` | `NOT NULL` | `LocalDateTime updatedAt` | Auto-updated modification timestamp |

---

## 2. Spring Boot Profile & Configuration Externalization

The application supports multiple execution profiles to isolate production environments from test pipelines.

### Default Configuration: `src/main/resources/application.properties`
*Uses environment variables with fallback defaults for local PostgreSQL.*

```properties
server.port=${SERVER_PORT:9090}

# Database Connection (PostgreSQL)
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/cirt_db}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Actuator & Prometheus
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
```

### CI Profile: `src/main/resources/application-ci.properties`
*Activated via `--spring.profiles.active=ci` for zero-dependency testing with H2.*

```properties
spring.datasource.url=jdbc:h2:mem:cirt_ci;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false

management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
```

---

## 3. Database Initialization & Local PostgreSQL Commands

### PostgreSQL Setup Commands
```sql
-- Execute in psql as postgres superuser
CREATE DATABASE cirt_db;
CREATE USER cirt_user WITH ENCRYPTED PASSWORD 'YOUR_SECURE_PASSWORD';
GRANT ALL PRIVILEGES ON DATABASE cirt_db TO cirt_user;
```

---

## 4. Running and Validating Application Setup

### Running with PostgreSQL (Default)
```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/cirt_db"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_actual_password"
mvn spring-boot:run
```

### Running with In-Memory H2 (CI Mode)
```powershell
java -jar target/cirt-devops-1.0.0-SNAPSHOT.jar --spring.profiles.active=ci
```

### Health Check Verification
```powershell
curl http://localhost:9090/actuator/health
```
**Expected Response**:
```json
{"status":"UP","components":{"db":{"status":"UP","details":{"database":"H2","validationQuery":"isValid()"}},"ping":{"status":"UP"}}}
```
