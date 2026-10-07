# Week 6 — Application & REST API Implementation

## Overview
This document details the software design, business logic, MVC web controllers, REST API endpoints, DTO validation rules, and automated test coverage implemented for the CIRT application.

---

## 1. Application Layered Architecture

The application adheres to a clean layered Spring architecture:

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
 └── PostgreSQL / H2 Memory Database
```

---

## 2. REST API Specification (`/api/incidents`)

| HTTP Method | Endpoint Path | Query Parameters | Request Body | HTTP Status | Description |
|---|---|---|---|---|---|
| `GET` | `/api/incidents` | `search`, `severity`, `status`, `category` | None | `200 OK` | Retrieves incidents with optional search & filter |
| `GET` | `/api/incidents/{id}` | None | None | `200 OK` / `404 Not Found` | Retrieves single incident by ID |
| `POST` | `/api/incidents` | None | `IncidentDto` (JSON) | `201 Created` / `400 Bad Request` | Creates new incident with DTO validation |
| `PUT` | `/api/incidents/{id}` | None | `IncidentDto` (JSON) | `200 OK` / `404 Not Found` | Updates existing incident record |
| `DELETE` | `/api/incidents/{id}` | None | None | `204 No Content` / `404 Not Found` | Deletes incident by ID |
| `PATCH` | `/api/incidents/{id}/status` | `status` | None | `200 OK` / `400 Bad Request` | Updates incident status lifecycle |
| `GET` | `/api/incidents/stats` | None | None | `200 OK` | Returns total, open, critical, and resolved counts |
| `GET` | `/api/incidents/alerts` | None | None | `200 OK` | Returns unresolved critical or unassigned incidents |

---

## 3. Web Views & Controller Routes (`IncidentController`)

| Route Path | Method | Template File | View Description |
|---|---|---|---|
| `/` | `GET` | `dashboard.html` | Real-time statistics overview and critical alerts |
| `/incidents` | `GET` | `incident-list.html` | Incident catalog with filter controls |
| `/incidents/new` | `GET` | `incident-form.html` | Incident report form |
| `/incidents` | `POST` | Redirect | Processes form submission and redirects |
| `/incidents/{id}` | `GET` | `incident-detail.html` | Detailed view with status transition controls |
| `/incidents/{id}/status` | `POST` | Redirect | Updates status and redirects to detail view |

---

## 4. DTO Validation Rules (`com.cirt.dto.IncidentDto`)

Input payloads for creating/updating incidents are strictly validated using Spring Boot starter validation annotations:

- **`title`**: `@NotBlank(message = "Title is required")`
- **`description`**: `@NotBlank(message = "Description is required")`
- **`category`**: `@NotNull(message = "Category is required")`
- **`severity`**: `@NotNull(message = "Severity is required")`
- **`reportedBy`**: `@NotBlank(message = "Reporter name is required")`

---

## 5. Automated Unit & Integration Test Suite

The baseline project includes 31 automated JUnit 5 unit and controller tests:

1. **`com.cirt.service.IncidentServiceTest` (17 tests)**:
   - Uses Mockito to test CRUD business logic, status transitions, search filtering, and statistics generation in memory.
2. **`com.cirt.controller.IncidentApiControllerTest` (14 tests)**:
   - Uses Spring `MockMvc` to test REST endpoints, JSON serialization, path variables, query params, and HTTP error response codes.

### Running Test Suite
```powershell
mvn test
```
**Expected Output**:
```text
Tests run: 31, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```
