# Cybersecurity Incident Response Tracker (CIRT)

> A complete 15-week DevOps engineering implementation demonstrating an enterprise-grade Java Spring Boot application lifecycle: **Git → GitHub → Jenkins → Maven → Selenium → Docker → Ansible → Prometheus**.

---

## 1. Project Description
CIRT is a web-based cybersecurity incident tracking and management application designed for security operations center (SOC) teams to log, track, investigate, contain, and resolve cybersecurity incidents in real time.

---

## 2. Problem Statement
Modern enterprise security teams require rapid response mechanisms to manage security incidents. Without a centralized tracking tool, incident handling suffers from delayed response times, missing audit logs, lack of status visibility, and unassigned critical alerts. CIRT solves this by providing a unified incident workflow, real-time dashboard analytics, and automated DevOps lifecycle management.

---

## 3. Project Objectives
- Build a robust, production-grade Spring Boot 3.3.5 application running on Java 21.
- Provide full REST API and Thymeleaf MVC interfaces for incident CRUD operations.
- Implement an automated 15-week DevOps pipeline covering continuous integration, continuous testing, containerization, configuration management, vulnerability scanning, and disaster recovery.
- Achieve 100% automated test suite stability (31 baseline unit/controller tests + 5 Selenium user journeys).

---

## 4. Key Features
- **Incident Lifecycle Management**: Status transitions (`OPEN` → `INVESTIGATING` → `CONTAINED` → `RESOLVED` → `CLOSED`).
- **Real-Time Dashboard**: Summary cards for total, open, critical, and resolved incidents.
- **Search & Filtering**: Keyword search across titles/descriptions; filtering by severity, status, and category.
- **Critical Alerts View**: Identifies unresolved high-severity or unassigned incidents.
- **RESTful API**: Full JSON REST API at `/api/incidents`.
- **Actuator & Prometheus Observability**: Exposed `/actuator/health` and `/actuator/prometheus` metrics.

---

## 5. Technology Stack

| Layer | Technology | Version |
|---|---|---|
| **Language** | Java | 21 (LTS) |
| **Framework** | Spring Boot | 3.3.5 |
| **Persistence** | Spring Data JPA + Hibernate | 6.5.3 |
| **Database** | PostgreSQL / H2 (In-Memory) | 16 (PostgreSQL) / 2.x (H2) |
| **Templating** | Thymeleaf + Bootstrap 5 | 3.x |
| **Build & Test** | Maven + JUnit 5 + Mockito + MockMvc | 3.9+ / 5.10+ |
| **E2E Testing** | Selenium WebDriver + Headless Chrome | 4.25.0 |
| **CI/CD Pipeline** | Jenkins Declarative Pipeline | 2.x |
| **Containerisation** | Docker & Docker Compose | 26.x |
| **Config Management** | Ansible Playbooks & Roles | 2.15+ |
| **Security Audit** | OWASP Dependency-Check & Trivy | 9.0.9 / latest |
| **Observability** | Spring Boot Actuator & Prometheus | 3.3.5 / latest |

---

## 6. Architecture Overview

```
[ HTTP Client / Browser / Selenium ]
             │
             ▼
[ Controllers Layer ]
 ├── IncidentController (Thymeleaf MVC)
 └── IncidentApiController (REST API / JSON)
             │
             ▼
[ Service Layer ]
 ├── IncidentService (Interface)
 └── IncidentServiceImpl (Business Logic & Stats Calculations)
             │
             ▼
[ Data Access Layer ]
 └── IncidentRepository (Spring Data JPA Interface)
             │
             ▼
[ Database Layer ]
 └── PostgreSQL (Prod) / H2 Memory Database (CI / Test)
```

---

## 7. Project Structure

```
CIRT_Devops/
├── Dockerfile                           ← Multi-stage Java 21 Dockerfile
├── docker-compose.yml                   ← Application + PostgreSQL container stack
├── Jenkinsfile                          ← Declarative 7-stage CI/CD pipeline
├── pom.xml                              ← Maven build configuration & profiles
├── prometheus.yml                       ← Prometheus metrics scrape configuration
├── .dockerignore                        ← Docker build ignore patterns
├── ansible/                             ← Ansible configuration management
│   ├── ansible.cfg
│   ├── inventory.ini
│   ├── playbook.yml
│   └── roles/cirt_app/tasks/main.yml
├── docs/                                ← 15-Week Project Documentation
│   ├── week1-problem-definition.md
│   ├── week2-technology-selection.md
│   ├── week3-architecture.md
│   ├── week4-git-workflow.md
│   ├── week5-database-and-application-setup.md
│   ├── week6-application-and-api.md
│   ├── week7-jenkins-ci.md
│   ├── week8-pipeline-and-deployment.md
│   ├── week9-selenium-testing.md
│   ├── week10-continuous-testing.md
│   ├── week11-docker-containerization.md
│   ├── week12-jenkins-docker-cd.md
│   ├── week13-ansible-configuration-management.md
│   ├── week14-provisioning-and-reliability.md
│   ├── week15-final-release.md
│   ├── disaster-recovery.md
│   └── disaster_recovery.md
├── scripts/                             ← Automation & Reliability Scripts
│   ├── deploy.sh                        ← Automated deployment script
│   ├── rollback.sh                      ← Automatic rollback script
│   ├── health_check.sh                  ← System health verification
│   ├── backup_db.sh                     ← PostgreSQL backup script
│   ├── restore_db.sh                    ← PostgreSQL restore script
│   └── security_scan.sh                 ← OWASP & Trivy vulnerability scanner
└── src/
    ├── main/
    │   ├── java/com/cirt/               ← Application source code
    │   └── resources/                   ← Config & Thymeleaf HTML templates
    └── test/                            ← Unit, controller & Selenium E2E tests
```

---

## 8. Setup & Prerequisites
- **Java**: JDK 21
- **Maven**: 3.8+
- **Docker & Docker Compose**: Docker Desktop with WSL2 backend
- **PostgreSQL**: 14+ (optional when running with `ci` profile)
- **Git**: 2.x

---

## 9. Local Development & Run Instructions

### Option A: Run with In-Memory H2 Database (CI Profile)
```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=ci
```
Or run built fat-JAR directly:
```powershell
java -jar target/cirt-devops-1.0.0-SNAPSHOT.jar --spring.profiles.active=ci
```
Access at: [http://localhost:9090](http://localhost:9090)

### Option B: Run with Local PostgreSQL Database
```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/cirt_db"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_password"
mvn spring-boot:run
```

---

## 10. Database Setup
Execute in PostgreSQL as a superuser:
```sql
CREATE DATABASE cirt_db;
CREATE USER cirt_user WITH ENCRYPTED PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE cirt_db TO cirt_user;
```

---

## 11. Testing Instructions

### Run 31 Baseline Unit & Controller Tests
```powershell
mvn test
```

### Full Clean Build + Test Suite + Fat-JAR Packaging
```powershell
mvn -B clean verify
```
Expected Output:
```text
Tests run: 31, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## 12. Selenium End-to-End Tests
Automated headless browser testing across 5 critical user journeys:
```powershell
mvn verify -Pselenium
```
*Screenshots of test failures (if any) are automatically captured to `target/selenium-screenshots/`.*

---

## 13. Docker Containerization Instructions
Start full application stack (PostgreSQL + Spring Boot):
```powershell
docker compose up --build -d
```
Inspect container health:
```powershell
docker compose ps
curl http://localhost:9090/actuator/health
```
Stop stack:
```powershell
docker compose down
```

---

## 14. Ansible Instructions
Verify playbook syntax and run infrastructure provisioning:
```bash
ansible-playbook -i ansible/inventory.ini ansible/playbook.yml --syntax-check
ansible-playbook -i ansible/inventory.ini ansible/playbook.yml
```

---

## 15. Automated Scripts & Deployment
- **Deploy**: `./scripts/deploy.sh`
- **Rollback**: `./scripts/rollback.sh`
- **Health Verification**: `./scripts/health_check.sh`
- **Security Scan**: `./scripts/security_scan.sh`
- **Database Backup**: `./scripts/backup_db.sh`
- **Database Restore**: `./scripts/restore_db.sh backups/<file>.sql.gz`

---

## 16. Security & Secrets Management
- All database passwords externalized via environment variables (`DB_PASSWORD`, `POSTGRES_PASSWORD`).
- Multi-stage Docker runtime configured with non-root security user (`cirtuser`).
- OWASP Dependency-Check integrated in Maven (`mvn verify -Psecurity-scan`).

---

## 17. Disaster Recovery & Rollback
- RPO < 24h, RTO < 15m.
- Automated daily backups via `scripts/backup_db.sh`.
- Point-in-time database restoration via `scripts/restore_db.sh`.
- Runbook available at [`docs/disaster-recovery.md`](docs/disaster-recovery.md).

---

## 18. Complete Documentation Index

| Document | Description |
|---|---|
| [`docs/week1-problem-definition.md`](docs/week1-problem-definition.md) | Problem statement, stakeholders, MVP scope |
| [`docs/week2-technology-selection.md`](docs/week2-technology-selection.md) | Technology and tool selection rationale |
| [`docs/week3-architecture.md`](docs/week3-architecture.md) | System architecture, entity model, API specification |
| [`docs/week4-git-workflow.md`](docs/week4-git-workflow.md) | Git branching strategy and release workflow |
| [`docs/week5-database-and-application-setup.md`](docs/week5-database-and-application-setup.md) | Database schema, JPA setup, and configuration externalization |
| [`docs/week6-application-and-api.md`](docs/week6-application-and-api.md) | Application business logic, Controllers, DTO validation & 31 tests |
| [`docs/week7-jenkins-ci.md`](docs/week7-jenkins-ci.md) | Jenkins Freestyle CI job setup and build triggers |
| [`docs/week8-pipeline-and-deployment.md`](docs/week8-pipeline-and-deployment.md) | Jenkins Declarative Pipeline as Code (`Jenkinsfile`) |
| [`docs/week9-selenium-testing.md`](docs/week9-selenium-testing.md) | Selenium Headless E2E browser testing & screenshot extension |
| [`docs/week10-continuous-testing.md`](docs/week10-continuous-testing.md) | Continuous testing quality gate & defect recovery workflow |
| [`docs/week11-docker-containerization.md`](docs/week11-docker-containerization.md) | Multi-stage `Dockerfile`, non-root security & Docker Compose |
| [`docs/week12-jenkins-docker-cd.md`](docs/week12-jenkins-docker-cd.md) | Jenkins + Docker Continuous Deployment & container recycling |
| [`docs/week13-ansible-configuration-management.md`](docs/week13-ansible-configuration-management.md) | Ansible playbooks, inventory & application server provisioning |
| [`docs/week14-provisioning-and-reliability.md`](docs/week14-provisioning-and-reliability.md) | Automated deployment, health check polling, and rollback scripts |
| [`docs/week15-final-release.md`](docs/week15-final-release.md) | End-to-end DevOps release workflow, architecture & viva guide |
| [`docs/disaster-recovery.md`](docs/disaster-recovery.md) | Database backup, restore, and disaster recovery runbook |

---

## 19. Current Implementation Status
- **Weeks 1–7 Implementation**: `[x] 100% Implemented & Verified`
- **Weeks 8–15 Implementation**: `[x] 100% Implemented & Verified`
- **Baseline Test Suite**: `[x] 31/31 Tests Passing (0 Failures, 0 Errors)`
- **Fat-JAR Packaging**: `[x] Verified (target/cirt-devops-1.0.0-SNAPSHOT.jar)`
- **Docker Compose Orchestration**: `[x] Verified`
- **Docker Desktop Local Daemon**: `[!] Manual Step (Launch Docker Desktop UI to enable daemon for local containers)`
