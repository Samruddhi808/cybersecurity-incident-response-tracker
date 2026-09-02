# Week 1 — Problem Definition and Scope

## Problem Statement

Security teams in organisations deal with cybersecurity incidents daily. Without a structured system, incidents are tracked in spreadsheets, emails, or informally — leading to:

- Incidents being forgotten or not escalated
- No visibility into which incidents are critical
- Difficulty tracking who is responsible for what
- No historical record of how incidents were resolved

The **Cybersecurity Incident Response Tracker (CIRT)** solves this by providing a simple, structured, web-based system for the complete incident lifecycle.

---

## Target Users

| User | Role |
|---|---|
| Security Analyst | Reports and investigates incidents |
| Team Lead / Manager | Assigns incidents, monitors dashboard |
| IT Administrator | Views incidents, updates status |

---

## Pain Points Addressed

1. **No central visibility** — All incidents in one dashboard
2. **No prioritisation** — Severity levels (LOW → CRITICAL)
3. **No accountability** — Assignment tracking
4. **No lifecycle management** — Status workflow (OPEN → CLOSED)
5. **No alerting** — Exception view for critical/unresolved incidents

---

## Stakeholders

| Stakeholder | Interest |
|---|---|
| Security Team | Primary users — report and resolve incidents |
| Management | Visibility into security posture |
| Development Team | Builds and maintains the system |
| Academic Assessors | Evaluate DevOps implementation |

---

## Constraints

| Constraint | Detail |
|---|---|
| Timeline | 15 weeks |
| Team size | 1 student developer |
| Technology | Defined by course requirements |
| Complexity | Keep it simple — academic project |
| Authentication | Not in scope for MVP |

---

## Project Objectives

1. Build a functional incident reporting and tracking system
2. Demonstrate a complete DevOps lifecycle (Git → Jenkins → Docker → Ansible)
3. Write automated tests that run in CI
4. Document every phase clearly for viva presentation

---

## Measurable Success Criteria

| Criterion | Target |
|---|---|
| Incidents can be created, viewed, updated | ✅ Week 5–6 |
| Dashboard shows live statistics | ✅ Week 6 |
| `mvn -B clean verify` passes | ✅ Week 7 |
| Jenkins build shows BUILD SUCCESS | ✅ Week 7 |
| Selenium tests pass in Jenkins | Target Week 10 |
| Docker image builds and runs | Target Week 11 |
| Ansible provisions a clean environment | Target Week 13 |

---

## MVP Scope (Weeks 3–6)

| Feature | In MVP |
|---|---|
| Incident creation | ✅ |
| Incident listing | ✅ |
| Dashboard statistics | ✅ |
| Search | ✅ |
| Filter by severity/status/category | ✅ |
| Incident detail view | ✅ |
| Status update | ✅ |
| Exception/alert view | ✅ |
| REST API | ✅ |
| User authentication | ❌ (not in MVP) |
| Email notifications | ❌ (not in MVP) |
| Reporting/export | ❌ (not in MVP) |
