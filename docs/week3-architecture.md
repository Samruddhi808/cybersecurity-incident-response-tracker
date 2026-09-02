# Week 3 — Requirements, Architecture and Technology Setup

## Architecture Overview

CIRT uses a standard **layered monolithic Spring Boot architecture**. This is intentionally simple — it avoids unnecessary complexity while being realistic enough to demonstrate the full DevOps lifecycle.

```
Browser (HTML + Bootstrap 5)
        │
        ▼
  Spring MVC Controllers
  ┌─────────────────────────────────────┐
  │  IncidentController   (Thymeleaf)   │
  │  IncidentApiController (REST/JSON)  │
  └─────────────────────────────────────┘
        │
        ▼
  Service Layer
  ┌─────────────────────────────────────┐
  │  IncidentService (interface)        │
  │  IncidentServiceImpl (logic)        │
  └─────────────────────────────────────┘
        │
        ▼
  Repository Layer
  ┌─────────────────────────────────────┐
  │  IncidentRepository (Spring Data)   │
  └─────────────────────────────────────┘
        │
        ▼
  Database: PostgreSQL (incidents table)
```

---

## Technology Decisions

| Decision | Reason |
|---|---|
| Spring Boot monolith | Simple, testable, DevOps-ready without microservice complexity |
| PostgreSQL | Industry standard relational DB; chosen over H2 in production |
| H2 (test scope only) | Allows automated tests without a live database |
| Thymeleaf | Server-side rendering; no frontend framework complexity |
| Bootstrap 5 | Clean, responsive UI without writing custom CSS frameworks |
| JUnit 5 + Mockito | Standard Java testing; Mockito enables unit tests without Spring |
| Maven | Standard Java build tool; well-supported by Jenkins |

---

## Data Model

### Incident Entity

```
incidents (table)
├── id           BIGINT PRIMARY KEY AUTO_INCREMENT
├── title        VARCHAR(200) NOT NULL
├── description  TEXT NOT NULL
├── category     VARCHAR(30) NOT NULL  (enum: stored as string)
├── severity     VARCHAR(20) NOT NULL  (enum: stored as string)
├── status       VARCHAR(20) NOT NULL  (enum: stored as string)
├── reported_by  VARCHAR(255) NOT NULL
├── assigned_to  VARCHAR(255) NULL
├── created_at   TIMESTAMP NOT NULL
├── updated_at   TIMESTAMP NOT NULL
└── resolution   TEXT NULL
```

### Enums

```java
enum Category {
    MALWARE, PHISHING, UNAUTHORIZED_ACCESS, DATA_BREACH,
    DENIAL_OF_SERVICE, SUSPICIOUS_ACTIVITY, OTHER
}

enum Severity { LOW, MEDIUM, HIGH, CRITICAL }

enum Status { OPEN, INVESTIGATING, CONTAINED, RESOLVED, CLOSED }
```

---

## API List

### MVC Pages (Thymeleaf)

| Method | URL | Description |
|---|---|---|
| GET | `/` | Dashboard |
| GET | `/incidents` | Incident list (+ search/filter params) |
| GET | `/incidents/new` | Create form |
| POST | `/incidents` | Submit create form |
| GET | `/incidents/{id}` | Detail view |
| GET | `/incidents/{id}/edit` | Edit form |
| POST | `/incidents/{id}/edit` | Submit edit form |
| POST | `/incidents/{id}/status` | Quick status update |
| GET | `/alerts` | Alert/exception view |

### REST API

| Method | URL | Description |
|---|---|---|
| GET | `/api/incidents` | List all (supports `?search=`, `?severity=`, `?status=`, `?category=`) |
| GET | `/api/incidents/{id}` | Get one |
| POST | `/api/incidents` | Create |
| PUT | `/api/incidents/{id}` | Update |
| DELETE | `/api/incidents/{id}` | Delete |
| PATCH | `/api/incidents/{id}/status` | Status update |
| GET | `/api/incidents/stats` | Dashboard statistics |
| GET | `/api/incidents/alerts` | Alert incidents |

---

## Development Environment Setup

### Prerequisites

```
Java 21 JDK
Apache Maven 3.8+
PostgreSQL 14+
Git 2.x
IDE: IntelliJ IDEA / VS Code / Eclipse
```

### Verify installation

```bash
java -version     # Should show Java 21
mvn -version      # Should show Maven 3.8+
psql --version    # Should show PostgreSQL 14+
git --version     # Any recent version
```
