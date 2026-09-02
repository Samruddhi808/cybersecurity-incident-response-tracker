# Cybersecurity Incident Response Tracker (CIRT)

> A college engineering project demonstrating a complete DevOps lifecycle using a real-world Java Spring Boot application.

---

## Project Overview

CIRT is a web-based application that allows a security team to:

- Report cybersecurity incidents
- Track incidents through their lifecycle (OPEN → INVESTIGATING → CONTAINED → RESOLVED → CLOSED)
- Search and filter incidents by severity, status, and category
- View a real-time dashboard with statistics
- Identify critical/unresolved incidents that require immediate attention

This is a **15-week academic project** designed to demonstrate not only software development but the complete **DevOps pipeline**: Git → GitHub → Jenkins → Maven → Docker → Ansible.

---

## Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3.5 |
| Web | Spring MVC + Thymeleaf |
| Persistence | Spring Data JPA + Hibernate |
| Database | PostgreSQL |
| Frontend | HTML + CSS + JavaScript + Bootstrap 5 |
| Build | Maven |
| Testing | JUnit 5 + Mockito + MockMvc |
| Version Control | Git + GitHub |
| CI | Jenkins (Freestyle + Jenkinsfile) |
| Containerisation | Docker |
| Config Management | Ansible |

---

## Features

| Feature | Description |
|---|---|
| Incident Creation | Form with validation (title, description, category, severity, reporter, assignee) |
| Incident Listing | Full table view with ID, severity/status badges, assignee, created date |
| Search | Case-insensitive search across title and description |
| Filter | Filter by severity, status, and/or category |
| Incident Detail | Full information view with inline status update |
| Status Update | Lifecycle transitions: OPEN → INVESTIGATING → CONTAINED → RESOLVED → CLOSED |
| Dashboard | Stats cards: total, open, critical, resolved |
| Alerts | Identifies critical/unassigned/overdue incidents requiring immediate attention |
| REST API | Full CRUD API at `/api/incidents` with JSON responses |

---

## Project Structure

```
src/
├── main/
│   ├── java/com/cirt/
│   │   ├── CirtApplication.java          ← Spring Boot entry point
│   │   ├── controller/
│   │   │   ├── IncidentController.java   ← MVC (Thymeleaf pages)
│   │   │   └── IncidentApiController.java← REST API (JSON)
│   │   ├── service/
│   │   │   ├── IncidentService.java      ← interface
│   │   │   └── IncidentServiceImpl.java  ← business logic
│   │   ├── repository/
│   │   │   └── IncidentRepository.java   ← Spring Data JPA
│   │   ├── model/
│   │   │   ├── Incident.java             ← JPA entity
│   │   │   ├── Category.java             ← enum
│   │   │   ├── Severity.java             ← enum
│   │   │   └── Status.java               ← enum
│   │   └── dto/
│   │       └── IncidentDto.java          ← validated input object
│   └── resources/
│       ├── templates/                    ← Thymeleaf HTML pages
│       ├── static/css/style.css
│       ├── static/js/main.js
│       └── application.properties
└── test/
    └── java/com/cirt/
        ├── service/IncidentServiceTest.java     ← 17 unit tests (Mockito)
        └── controller/IncidentApiControllerTest.java ← 14 controller tests (MockMvc)
```

---

## Prerequisites

- Java 21 (JDK)
- Maven 3.8+
- PostgreSQL 14+
- Git

---

## Database Setup

```sql
-- Run in PostgreSQL as a superuser
CREATE DATABASE cirt_db;
CREATE USER cirt_user WITH PASSWORD 'yourpassword';
GRANT ALL PRIVILEGES ON DATABASE cirt_db TO cirt_user;
```

JPA will automatically create the `incidents` table on first startup.

---

## Running Locally

### 1. Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/CIRT_Devops.git
cd CIRT_Devops
```

### 2. Configure database credentials

**Option A — Environment Variables (recommended)**

```bash
# Windows PowerShell
$env:DB_URL="jdbc:postgresql://localhost:5432/cirt_db"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="yourpassword"
```

**Option B — Edit `application.properties` directly** *(do not commit real passwords)*

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cirt_db
spring.datasource.username=postgres
spring.datasource.password=yourpassword
```

### 3. Start the application

```bash
mvn spring-boot:run
```

Open: [http://localhost:8080](http://localhost:8080)

---

## Running Tests

Tests use an **H2 in-memory database** — no PostgreSQL required.

```bash
mvn test
```

### Full build + test + package

```bash
mvn -B clean verify
```

Expected output:

```
Tests run: 31, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## Maven Commands

| Command | Purpose |
|---|---|
| `mvn clean compile` | Compile only |
| `mvn test` | Run all unit/integration tests |
| `mvn -B clean verify` | Full build + test + package (used by Jenkins) |
| `mvn spring-boot:run` | Run the application locally |
| `mvn package` | Build the runnable JAR |

---

## REST API Reference

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/incidents` | List all incidents |
| `GET` | `/api/incidents?search=phishing` | Search incidents |
| `GET` | `/api/incidents?severity=CRITICAL` | Filter incidents |
| `GET` | `/api/incidents/{id}` | Get one incident |
| `POST` | `/api/incidents` | Create incident |
| `PUT` | `/api/incidents/{id}` | Update incident |
| `DELETE` | `/api/incidents/{id}` | Delete incident |
| `PATCH` | `/api/incidents/{id}/status?status=RESOLVED` | Update status only |
| `GET` | `/api/incidents/stats` | Dashboard statistics |
| `GET` | `/api/incidents/alerts` | Alert incidents |

---

## Git Branch Strategy

| Branch | Purpose |
|---|---|
| `main` | Stable, release-ready code |
| `development` | Integration branch |
| `feature/incident-management` | Incident CRUD features |
| `feature/dashboard` | Dashboard feature |
| `feature/search-filter` | Search and filter |
| `feature/alerts` | Alert/exception view |

---

## Jenkins Setup (Week 7)

See [`docs/week7-jenkins-ci.md`](docs/week7-jenkins-ci.md) for full Jenkins Freestyle CI configuration.

**Build command used by Jenkins:**

```bash
mvn -B clean verify
```

**Archived artifact:** `target/*.jar`

---

## Documentation

| Document | Description |
|---|---|
| [`docs/week1-problem-definition.md`](docs/week1-problem-definition.md) | Problem statement, stakeholders, MVP scope |
| [`docs/week3-architecture.md`](docs/week3-architecture.md) | Architecture, data model, API list |
| [`docs/week4-git-workflow.md`](docs/week4-git-workflow.md) | Git branching strategy and workflow |
| [`docs/week7-jenkins-ci.md`](docs/week7-jenkins-ci.md) | Jenkins CI configuration guide |

---

## Future Weeks (Not Implemented Yet)

- **Week 8** — Jenkinsfile pipeline as code
- **Week 9** — Selenium end-to-end tests
- **Week 10** — Selenium tests in Jenkins pipeline
- **Week 11** — Docker containerisation
- **Week 12** — Jenkins + Docker CD
- **Week 13** — Ansible configuration management
- **Week 14** — Automated provisioning
- **Week 15** — Final end-to-end release
