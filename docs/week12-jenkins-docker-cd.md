# Week 12 — Continuous Deployment with Jenkins & Docker

## Overview
This document details the automated continuous deployment (CD) architecture combining Jenkins pipeline stages, dynamic Docker image tagging, container recycling strategies, and environment credential injection.

---

## 1. Continuous Deployment Pipeline Flow

```
[ Jenkins Build Stage ]
         │
         ▼
[ Docker Build & Tag ]
 ├── Image Tag: cirt-devops:${BUILD_NUMBER}
 └── Image Tag: cirt-devops:latest
         │
         ▼
[ Automated Container Recycling ]
 ├── docker compose down (stops previous container version)
 └── docker compose up -d (launches fresh container with updated image)
         │
         ▼
[ Post-Deployment Health Check Gate ]
 ├── GET http://localhost:9090/actuator/health
 └── Verify Status == "UP"
         │
    ┌────┴───────────────┐
    ▼                    ▼
[ SUCCESS ]        [ FAILURE ]
Keep Stack       Invoke Rollback Script (scripts/rollback.sh)
```

---

## 2. Jenkinsfile Deployment Implementation

The deployment stage in [`Jenkinsfile`](../Jenkinsfile) manages container replacement:

```groovy
stage('Deploy to Staging Environment') {
    steps {
        echo 'Deploying application to Staging environment using Docker Compose...'
        script {
            if (isUnix()) {
                sh 'docker compose down || true'
                sh 'docker compose up -d'
            } else {
                bat 'docker compose down || true'
                bat 'docker compose up -d'
            }
        }
    }
}
```

---

## 3. Secret & Credential Handling Best Practices

- **Database Passwords**: Externalized into environment variables (`DB_PASSWORD`, `POSTGRES_PASSWORD`).
- **Jenkins Credentials Store**: Passwords injected into pipeline steps using Jenkins `withCredentials([usernamePassword(...)])` wrappers.
- **Tracked Code Safety**: Verified that no raw credentials exist in `application.properties` or git tracking.

---

## 4. Fresh Container Replacement & Health Verification

1. **Graceful Termination**: `docker compose down` sends `SIGTERM` to the container process, giving Spring Boot 30 seconds to finish active requests before closing JPA connections.
2. **Atomic Up**: `docker compose up -d` recreates the `cirt-app` container bound to network port `9090`.
3. **Health Validation**: The container's built-in Docker `HEALTHCHECK` polls `http://localhost:9090/actuator/health` until HTTP `200 OK` status is returned.
