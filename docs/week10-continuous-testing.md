# Week 10 — Continuous Testing & Quality Gate Integration

## Overview
This document details the continuous testing strategy, quality gate enforcement, test failure handling, and defect recovery workflow integrated into the CIRT Jenkins CI/CD pipeline.

---

## 1. Automated Quality Gate Architecture

The CIRT CI/CD pipeline implements a multi-tier quality gate to ensure broken code or failing user journeys cannot reach production:

```
[ Git Commit Push ]
        │
        ▼
[ Tier 1: Unit & Service Tests ] ──(Fail)──► [ BUILD FAILED: Stop Pipeline ]
 (31 Mockito/MockMvc Tests)
        │ (Pass)
        ▼
[ Tier 2: Selenium E2E Journeys ] ──(Fail)──► [ BUILD FAILED: Capture Screenshots & Stop ]
 (5 Headless Chrome User Journeys)
        │ (Pass)
        ▼
[ Tier 3: Security & Image Scan ] ──(Fail)──► [ BUILD FAILED: Stop Deployment ]
 (OWASP & Trivy Scan)
        │ (Pass)
        ▼
[ Deployment to Staging ]
```

---

## 2. Pipeline Test Execution Stage

In [`Jenkinsfile`](../Jenkinsfile), automated Selenium tests are executed during the `Integration & Selenium E2E Tests` stage:

```groovy
stage('Integration & Selenium E2E Tests') {
    steps {
        echo 'Running Selenium End-to-End Integration Tests...'
        script {
            if (isUnix()) {
                sh 'mvn verify -Pselenium -Dtest=false -B'
            } else {
                bat 'mvn verify -Pselenium -Dtest=false -B'
            }
        }
    }
}
```

---

## 3. Failure Behavior & Defect Recovery Demonstration

### Defect Injection Test Procedure (Verification)
1. **Inject Test Failure**: Temporarily modify an assertion in `IncidentServiceTest.java` or `IncidentWorkflowIT.java` (e.g. change expected count from `10L` to `999L`).
2. **Trigger Build**: Execute `mvn -B clean verify`.
3. **Observed Result**:
   ```text
   [ERROR] Failures:
   [ERROR]   IncidentServiceTest.getDashboardStats_returnsCorrectCounts:45 expected: <999> but was: <10>
   [INFO] BUILD FAILURE
   ```
4. **Pipeline Impact**: The build terminates immediately with exit code `1`. Deployment stages are skipped. Failure screenshots are generated at `target/selenium-screenshots/`.
5. **Recovery**: Revert the invalid assertion, re-run build:
   ```text
   Tests run: 31, Failures: 0, Errors: 0, Skipped: 0
   [INFO] BUILD SUCCESS
   ```

---

## 4. Test Result Archiving in Jenkins

The `post { always { ... } }` block in `Jenkinsfile` guarantees test reports are processed even on failure:
- **Surefire Reports**: `**/target/surefire-reports/*.xml` published to Jenkins test trend charts.
- **Failsafe Reports**: `**/target/failsafe-reports/*.xml` processed for integration test results.
- **Artifacts**: Failure PNG screenshots archived for debugging.
