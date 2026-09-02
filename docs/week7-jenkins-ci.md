# Week 7 — Jenkins Continuous Integration

## Purpose

In Week 7, we configure Jenkins to automatically:

1. Pull the CIRT source code from GitHub
2. Compile the project with Maven
3. Run all automated tests (31 tests)
4. Package the application as a runnable JAR
5. Archive the JAR as a build artifact

This is a **Jenkins Freestyle project** (not a Jenkinsfile — that comes in Week 8).

---

## Prerequisites

Before configuring Jenkins:

- Jenkins is installed and running (typically at `http://localhost:8080` or your server IP)
- The following Jenkins plugins are installed:
  - **Git plugin** — to pull from GitHub
  - **Maven Integration plugin** — to run Maven builds

### Verify Maven build works locally first

```bash
mvn -B clean verify
```

Expected:

```
Tests run: 31, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

And the JAR exists at:

```
target/cirt-devops-1.0.0-SNAPSHOT.jar
```

---

## Jenkins Configuration — Step by Step

### Step 1: Configure JDK and Maven in Jenkins

1. Go to **Manage Jenkins → Tools**
2. Under **JDK installations**:
   - Click "Add JDK"
   - Name: `Java 21`
   - Set JAVA_HOME to your JDK 21 path
3. Under **Maven installations**:
   - Click "Add Maven"
   - Name: `Maven 3`
   - Select "Install automatically" or point to local Maven

---

### Step 2: Create a New Freestyle Job

1. Click **New Item**
2. Name: `CIRT-CI`
3. Type: **Freestyle project**
4. Click OK

---

### Step 3: Configure Source Code Management

Under **Source Code Management**:

- Select **Git**
- Repository URL: `https://github.com/YOUR_USERNAME/CIRT_Devops.git`
- Branch: `*/main` (or `*/development`)
- Credentials: Add GitHub credentials if the repo is private

---

### Step 4: Configure Build Environment

Under **Build Environment**:

- Check "Delete workspace before build starts" *(ensures clean builds)*

---

### Step 5: Add Build Step

Under **Build**:

- Click "Add build step" → **Invoke top-level Maven targets**
- Maven version: `Maven 3`
- Goals: `clean verify`
- Additional options: `-B` (batch/non-interactive mode)

Full goals field: `-B clean verify`

---

### Step 6: Archive the JAR

Under **Post-build Actions**:

- Click "Add post-build action" → **Archive the artifacts**
- Files to archive: `target/*.jar`

---

### Step 7: Save and Run

- Click **Save**
- Click **Build Now**
- Click the build number → **Console Output**

Expected final lines:

```
Tests run: 31, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time: XX s
Finished: SUCCESS
```

---

## Demonstrating Failure and Recovery (Week 7 Lab Requirement)

### Step 1 — Introduce a deliberate test failure

Edit one test in `IncidentServiceTest.java` to assert the wrong value:

```java
// Change this:
assertEquals(10L, stats.get("total"));

// To this (wrong value — will fail):
assertEquals(99L, stats.get("total"));
```

Commit and push:

```bash
git add .
git commit -m "test: introduce deliberate failure for Jenkins demo"
git push
```

Trigger a Jenkins build → **Console Output should show:**

```
Tests run: 31, Failures: 1, Errors: 0, Skipped: 0
BUILD FAILURE
Finished: FAILURE
```

### Step 2 — Fix the test and demonstrate recovery

Revert the change:

```java
assertEquals(10L, stats.get("total"));
```

Commit and push:

```bash
git add .
git commit -m "test: fix deliberate failure — restore correct assertion"
git push
```

Trigger build again → **Console Output shows:**

```
Tests run: 31, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
Finished: SUCCESS
```

---

## Build Artifacts

After a successful build, the archived artifact is visible in Jenkins:

- Go to the build → **Build Artifacts**
- File: `target/cirt-devops-1.0.0-SNAPSHOT.jar`
- This JAR can be run directly: `java -jar cirt-devops-1.0.0-SNAPSHOT.jar`

---

## Screenshot Placeholders

*(Add actual screenshots here during the lab session)*

| Screenshot | Description |
|---|---|
| `jenkins-job-config.png` | Jenkins Freestyle job configuration |
| `jenkins-build-success.png` | Console output showing BUILD SUCCESS |
| `jenkins-build-failure.png` | Console output showing BUILD FAILURE |
| `jenkins-artifacts.png` | Archived JAR artifact view |

---

## What This Demonstrates

| DevOps Concept | Demonstrated By |
|---|---|
| Continuous Integration | Jenkins automatically builds on push |
| Automated Testing | 31 JUnit tests run without manual intervention |
| Build Reproducibility | `mvn -B clean verify` gives same result every time |
| Fast Feedback | Build failure immediately visible in Jenkins |
| Artifact Management | JAR archived after successful build |
