# Week 8 — Jenkins Pipeline as Code & Deployment

## Overview
This document details the Declarative Pipeline as Code implementation using a root [`Jenkinsfile`](../Jenkinsfile) to automate continuous integration, testing, security scanning, container packaging, and staging deployment.

---

## 1. Jenkinsfile Pipeline Structure

The pipeline is defined in [`Jenkinsfile`](../Jenkinsfile) and uses multi-platform shell execution (`isUnix()` check for Linux vs Windows agents):

```groovy
pipeline {
    agent any

    environment {
        APP_NAME    = 'cirt-devops'
        IMAGE_NAME  = 'cirt-devops'
        IMAGE_TAG   = "${BUILD_NUMBER}"
    }

    options {
        timeout(time: 1, unit: 'HOURS')
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Checkout') { ... }
        stage('Build & Unit Tests') { ... }
        stage('Package Artifact') { ... }
        stage('Container Image Build') { ... }
        stage('Integration & Selenium E2E Tests') { ... }
        stage('Security & Vulnerability Scan') { ... }
        stage('Deploy to Staging Environment') { ... }
    }

    post {
        always {
            junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
            archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
        }
    }
}
```

---

## 2. Pipeline Stages Detailed Specification

| Stage Name | Command Executed | Purpose / Output |
|---|---|---|
| **Checkout** | `checkout scm` | Clones target git commit branch into agent workspace |
| **Build & Unit Tests** | `mvn clean test -B` | Compiles source files, executes 31 baseline JUnit/Mockito tests |
| **Package Artifact** | `mvn package -DskipTests -B` | Generates executable fat-JAR at `target/cirt-devops-1.0.0-SNAPSHOT.jar` |
| **Container Image Build** | `docker build -t cirt-devops:${BUILD_NUMBER} -t cirt-devops:latest .` | Builds multi-stage production Docker image |
| **Integration & Selenium E2E Tests** | `mvn verify -Pselenium -Dtest=false -B` | Auto-starts app with H2, runs 5 Selenium browser journeys |
| **Security & Vulnerability Scan** | `docker run aquasec/trivy image cirt-devops:${BUILD_NUMBER}` | Scans container layers for `HIGH` and `CRITICAL` vulnerabilities |
| **Deploy to Staging Environment** | `docker compose down && docker compose up -d` | Launches production container stack (`cirt-app` + `cirt-db`) |

---

## 3. Required Jenkins Agent Setup

1. **Prerequisites**:
   - Java 21 JDK installed on agent host
   - Apache Maven 3.9+ installed and added to agent `PATH`
   - Docker Engine and Docker Compose installed and accessible by Jenkins user
2. **Jenkins Job Configuration**:
   - Create a **Pipeline** job in Jenkins GUI
   - Definition: *Pipeline script from SCM*
   - SCM: *Git* (Repository URL: `https://github.com/Samruddhi808/cybersecurity-incident-response-tracker.git`)
   - Script Path: `Jenkinsfile`

---

## 4. Expected Output & Post-Build Artifacts

Upon successful build execution:
- **Unit Test Results**: Saved and rendered in Jenkins JUnit report viewer from `target/surefire-reports/*.xml`.
- **Archived Artifact**: `target/cirt-devops-1.0.0-SNAPSHOT.jar` available for direct download from build page.
- **Docker Image**: Tagged as `cirt-devops:latest` and `cirt-devops:${BUILD_NUMBER}`.
