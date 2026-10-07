# Week 9 — Selenium End-to-End Testing

## Overview
This document details the automated browser end-to-end (E2E) testing implementation for CIRT using Selenium WebDriver, Headless Chrome, and JUnit 5.

---

## 1. Selenium Framework Architecture

- **Test Class**: [`com.cirt.selenium.IncidentWorkflowIT`](../src/test/java/com/cirt/selenium/IncidentWorkflowIT.java)
- **Maven Dependency**: `org.seleniumhq.selenium:selenium-java:4.25.0` (Scope: `test`)
- **Profile**: Maven `selenium` profile auto-starts Spring Boot with `ci` profile (H2 in-memory DB) on port `9090` before tests, and terminates the server after test completion.
- **Browser**: Headless Chrome (`--headless=new`, `--no-sandbox`, `--disable-dev-shm-usage`, `--window-size=1920,1080`)

---

## 2. Tested Critical User Journeys

| Order | Test Method | User Journey | Key Selectors / Operations | Assertions |
|---|---|---|---|---|
| **1** | `test1_dashboardLoadsWithStats()` | Dashboard loads statistics cards | Inspect `.card-title`, `.display-6` | Verifies dashboard title, total incidents card, open incidents card, critical alerts |
| **2** | `test2_createNewIncident()` | Report new incident via form | `input[name="title"]`, `textarea[name="description"]`, `select[name="category"]`, `select[name="severity"]`, `input[name="reportedBy"]` | Confirms form submission redirects to detail page and contains "Phishing Email Detected" |
| **3** | `test3_viewIncidentList()` | View incident list catalog | Navigate `/incidents`, inspect `#incidentTable` | Asserts table row contains created incident title and severity badge |
| **4** | `test4_updateIncidentStatus()` | Lifecycle status update | `select[name="status"]`, click status submit button | Asserts status transitions from `OPEN` to `INVESTIGATING` on detail page |
| **5** | `test5_searchIncidents()` | Search catalog by keyword | Input `#searchInput`, submit search | Asserts filtered table only displays matching keyword records |

---

## 3. Automatic Failure Screenshot Capture

The test class registers a custom JUnit 5 `TestWatcher` extension:

```java
@RegisterExtension
static TestWatcher screenshotWatcher = new TestWatcher() {
    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        captureScreenshot(context.getDisplayName() + "_FAILED");
    }
};
```
- **Output Directory**: `target/selenium-screenshots/`
- **File Naming Format**: `<Test_Display_Name>_FAILED.png`

---

## 4. Running Selenium Tests Locally

### Command
```powershell
mvn verify -Pselenium
```

### Execution Flow
1. Maven `spring-boot-maven-plugin:start` starts application with `ci` profile on `http://localhost:9090`.
2. Maven `maven-failsafe-plugin` executes `*IT.java` integration tests.
3. ChromeDriver launches headless browser and executes the 5 user journeys in sequence (`@TestMethodOrder(MethodOrderer.OrderAnnotation.class)`).
4. Maven `spring-boot-maven-plugin:stop` shuts down application process.
