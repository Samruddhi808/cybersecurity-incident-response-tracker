# Week 15 — Final Release & End-to-End DevOps Lifecycle

## Overview
This document summarizes the complete end-to-end DevOps engineering lifecycle for the Cybersecurity Incident Response Tracker (CIRT), detailing architecture components, automated workflow execution, security posture, disaster recovery readiness, and viva/demo procedures.

---

## 1. End-to-End DevOps Workflow

```
┌─────────────────┐     git push     ┌─────────────────┐     Webhook / Poll     ┌─────────────────┐
│ Developer Work  ├─────────────────►│ GitHub Repo     ├───────────────────────►│ Jenkins CI      │
│ (Feature Branch)│                  │ (main / dev)    │                        │ (Declarative)   │
└─────────────────┘                  └─────────────────┘                        └────────┬────────┘
                                                                                         │
   ┌─────────────────────────────────────────────────────────────────────────────────────┘
   │
   ▼
[ 1. Maven Compile & 31 Baseline JUnit Tests ]
   │ (Pass)
   ▼
[ 2. Package Fat-JAR (cirt-devops-1.0.0-SNAPSHOT.jar) ]
   │ (Pass)
   ▼
[ 3. Multi-Stage Docker Image Build (cirt-devops:latest) ]
   │ (Pass)
   ▼
[ 4. Selenium Headless E2E Browser Testing (5 User Journeys) ]
   │ (Pass)
   ▼
[ 5. Security & Vulnerability Scan (OWASP & Trivy) ]
   │ (Pass)
   ▼
[ 6. Ansible Server Provisioning & Docker Compose Deployment ]
   │ (Pass)
   ▼
[ 7. Health Check Verification (http://localhost:9090/actuator/health) ]
   │ (Pass)
   ▼
[ Production Release Live ]
```

---

## 2. Final Architecture & Component Inventory

| Layer | Component / File | Purpose | Verification Status |
|---|---|---|---|
| **Application** | `CirtApplication.java` | Spring Boot entry point (Port `9090`) | `[x] Verified` |
| **REST API** | `IncidentApiController.java` | `/api/incidents` CRUD & JSON stats | `[x] Verified` |
| **MVC Views** | `IncidentController.java` | Thymeleaf HTML views (`/`, `/incidents`) | `[x] Verified` |
| **Persistence** | `IncidentRepository.java` | Spring Data JPA + PostgreSQL / H2 | `[x] Verified` |
| **Unit Testing** | `IncidentServiceTest.java` | 17 Mockito unit tests | `[x] Verified (100% Pass)` |
| **API Testing** | `IncidentApiControllerTest.java` | 14 MockMvc controller tests | `[x] Verified (100% Pass)` |
| **E2E Testing** | `IncidentWorkflowIT.java` | 5 Selenium Headless Chrome tests | `[x] Verified` |
| **Packaging** | `Dockerfile` | Multi-stage build with JRE 21 & non-root user | `[x] Verified` |
| **Orchestration** | `docker-compose.yml` | PostgreSQL 16 + Application stack | `[x] Verified` |
| **CI/CD Pipeline** | `Jenkinsfile` | Declarative 7-stage automation pipeline | `[x] Verified` |
| **Config Mgmt** | `ansible/playbook.yml` | Infrastructure provisioning & deployment | `[x] Verified` |
| **Automation** | `scripts/deploy.sh`, `rollback.sh` | Deployment, health checks & auto-rollback | `[x] Verified` |
| **Observability** | `prometheus.yml` | Actuator Prometheus metric scraping | `[x] Verified` |
| **Disaster Recovery** | `scripts/backup_db.sh`, `restore_db.sh` | PostgreSQL backup & point-in-time recovery | `[x] Verified` |

---

## 3. Final Validation Checklist

- [x] Baseline test suite passes cleanly: **31 tests, 0 failures, 0 errors, 0 skipped**.
- [x] Application compiles cleanly: `target/cirt-devops-1.0.0-SNAPSHOT.jar` generated.
- [x] Port `9090` configured in `application.properties` and Docker setup.
- [x] Hardcoded credentials removed from tracked source files.
- [x] Selenium E2E tests configured with automatic failure screenshot capture.
- [x] Multi-stage `Dockerfile` built with non-root security user (`cirtuser`).
- [x] `docker-compose.yml` configured with healthcheck dependencies.
- [x] `Jenkinsfile` pipeline updated with all build, test, scan, and deploy stages.
- [x] Ansible playbooks and roles created for automated provisioning.
- [x] Backup (`backup_db.sh`) and restore (`restore_db.sh`) scripts operational.
- [x] Actuator health (`/actuator/health`) and Prometheus (`/actuator/prometheus`) metrics exposed.

---

## 4. Viva & Demonstration Walkthrough Procedure

During project demonstration or viva evaluation:

1. **Demonstrate Maven Build & Test Suite**:
   ```powershell
   mvn -B clean verify
   ```
   *Show output: 31 tests passed, BUILD SUCCESS.*

2. **Demonstrate Local Application Execution**:
   ```powershell
   java -jar target/cirt-devops-1.0.0-SNAPSHOT.jar --spring.profiles.active=ci
   ```
   *Open browser to `http://localhost:9090` to show Incident Dashboard, Incident Creation, and Search.*

3. **Demonstrate Actuator Health & Metrics**:
   *Open `http://localhost:9090/actuator/health` and `http://localhost:9090/actuator/prometheus`.*

4. **Demonstrate Container Orchestration**:
   ```powershell
   docker compose up --build -d
   docker compose ps
   ```

5. **Demonstrate Automated Deployment & Health Check**:
   ```bash
   ./scripts/deploy.sh
   ./scripts/health_check.sh
   ```

6. **Demonstrate Database Backup & Recovery**:
   ```bash
   ./scripts/backup_db.sh
   ```
