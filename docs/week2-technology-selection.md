# Week 2 — Technology Selection & Justification

## Overview
This document details the selection, technical evaluation, and justification of the software development tools, frameworks, build systems, and DevOps automation technologies for the Cybersecurity Incident Response Tracker (CIRT).

---

## 1. Core Technology Stack

| Layer / Domain | Selected Technology | Version | Selection Rationale | Role in CIRT Project |
|---|---|---|---|---|
| **Programming Language** | Java | 21 (LTS) | Strong static typing, virtual threads, high performance, enterprise adoption | Core language for application backend, business logic, and entity definitions |
| **Framework** | Spring Boot | 3.3.5 | Rapid boilerplate-free development, embedded Tomcat web server, native JPA & MVC integration | Main application framework handling dependency injection, REST APIs, and web views |
| **Build System** | Apache Maven | 3.9+ | Declarative lifecycle management (`pom.xml`), dependency management, plugin ecosystem | Compiles code, runs tests, packages executable fat-JAR, manages Selenium and OWASP plugins |
| **Relational Database** | PostgreSQL | 16 (Alpine) | ACID compliance, strong reliability, native JSON support, production standard | Persistent storage for incident records, user audit trails, and reporting stats in production |
| **In-Memory Database** | H2 Database | 2.x | Zero configuration, fast in-memory execution, isolated test contexts | Fast unit testing database and zero-dependency CI test environment (`ci` Spring profile) |
| **Template Engine** | Thymeleaf | 3.x | Natural HTML templating, tight Spring integration, server-side rendering | Renders server-side HTML views (`dashboard.html`, `incident-list.html`, `incident-detail.html`) |
| **Version Control** | Git & GitHub | 2.x | Distributed version control, feature branch isolation, pull request workflows | Source code management, team collaboration, and trigger source for Jenkins CI/CD |
| **CI/CD Automation** | Jenkins | 2.x | Declarative pipeline-as-code (`Jenkinsfile`), vast plugin ecosystem | Automated build, unit testing, packaging, containerization, security scanning, and deployment |
| **Browser Automation** | Selenium WebDriver | 4.25.0 | Cross-browser automated user journey validation, Java binding support | End-to-end user workflow testing (`IncidentWorkflowIT.java`) against running app |
| **Containerisation** | Docker & Docker Compose | 26.x | Portable lightweight container packaging, reproducible runtime environment | Multi-stage application packaging and two-tier container orchestration (`app` + `db`) |
| **Configuration Management** | Ansible | 2.15+ | Agentless YAML-based configuration, SSH execution, idempotent playbooks | Automated server provisioning, package installation, firewall setup, and app deployment |
| **Security Scanning** | OWASP Dependency-Check & Trivy | 9.0.9 / latest | Automated vulnerability detection in Maven dependencies and Docker container layers | Pre-deployment security quality gate in CI pipeline (`security-scan` profile) |
| **Metrics & Monitoring** | Spring Boot Actuator & Prometheus | 3.3.5 / latest | Standardized metric collection, health check endpoints, scrape configuration | Real-time monitoring of server health (`/actuator/health`) and metrics (`/actuator/prometheus`) |

---

## 2. Environment Specifications

### Development & Execution Environment
- **Operating System**: Windows 11 with WSL2 (Ubuntu 22.04 LTS)
- **Runtime Environment**: Java Development Kit (JDK) 21.0.x
- **Container Engine**: Docker Desktop with WSL2 backend
- **Application Server Port**: `9090`
- **Database Port**: `5432`

---

## 3. Technology Trade-off Analysis

1. **Spring Boot vs. Plain Servlet Architecture**:
   Spring Boot was chosen over raw Servlets to eliminate manual XML routing and database connection management through Spring Data JPA repositories.
2. **PostgreSQL + H2 Dual-Database Setup**:
   PostgreSQL provides enterprise persistence for production, while H2 allows unit and CI tests to run in seconds without external database dependencies.
3. **Jenkins Pipeline as Code vs. GUI Jobs**:
   Storing build logic in a version-controlled [`Jenkinsfile`](../Jenkinsfile) guarantees pipeline reproducibility and branch-level testing.
4. **Ansible vs. Shell Script Provisioning**:
   Ansible ensures idempotent server state management without risk of script failure on repeated execution.
