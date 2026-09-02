# Week 4 — Git/GitHub Repository Initialization

## Repository Setup

### Initialize Git

```bash
cd CIRT_Devops
git init
git add .
git commit -m "Initial project structure — Spring Boot skeleton"
```

### Connect to GitHub

```bash
git remote add origin https://github.com/YOUR_USERNAME/CIRT_Devops.git
git branch -M main
git push -u origin main
```

---

## Branch Strategy

CIRT uses a simple two-layer branching model:

```
main
 └── development
       ├── feature/incident-management
       ├── feature/dashboard
       ├── feature/search-filter
       └── feature/alerts
```

| Branch | Purpose |
|---|---|
| `main` | Stable, release-ready. Only merged into from `development`. |
| `development` | Integration branch. Features merge here first. |
| `feature/*` | One branch per feature. Deleted after merge. |

---

## Feature Branch Workflow

```bash
# 1. Create feature branch from development
git checkout development
git checkout -b feature/incident-management

# 2. Work on the feature, commit as you go
git add src/main/java/com/cirt/model/Incident.java
git commit -m "feat: add Incident entity with Category/Severity/Status enums"

git add src/main/java/com/cirt/service/
git commit -m "feat: implement IncidentService with create, update, search logic"

# 3. Push to GitHub
git push origin feature/incident-management

# 4. Create Pull Request on GitHub: feature/incident-management → development

# 5. Review and merge

# 6. Delete the feature branch
git branch -d feature/incident-management
```

---

## Commit Message Convention

Use a clear prefix to categorise commits:

| Prefix | Meaning | Example |
|---|---|---|
| `feat:` | New feature | `feat: add incident search endpoint` |
| `fix:` | Bug fix | `fix: correct status badge CSS class` |
| `test:` | Add/modify tests | `test: add service unit tests for alert logic` |
| `docs:` | Documentation | `docs: update README with Jenkins setup` |
| `refactor:` | Refactoring | `refactor: extract enum parsing to helper method` |
| `ci:` | CI/DevOps config | `ci: add Jenkinsfile pipeline stages` |
| `chore:` | Maintenance | `chore: update .gitignore` |

---

## .gitignore Key Exclusions

```
target/           ← Compiled output — never commit
*.class           ← Java bytecode
.env              ← Never commit credentials
application-local.properties  ← Local overrides
.idea/            ← IntelliJ project files
.vscode/          ← VS Code settings
```

---

## Demonstrating a Merge Conflict (Week 6 Task)

To demonstrate a controlled merge conflict:

```bash
# On branch-A, change a line in a file
git checkout -b conflict-demo-a
# edit IncidentServiceImpl.java — change a comment or variable name
git commit -m "chore: conflict demo branch A"

# On branch-B, change the same line differently
git checkout development
git checkout -b conflict-demo-b
# edit the same line differently
git commit -m "chore: conflict demo branch B"

# Merge branch-A into development
git checkout development
git merge conflict-demo-a

# Merge branch-B — this will conflict
git merge conflict-demo-b
# Git will show: CONFLICT in IncidentServiceImpl.java

# Manually resolve conflict, then:
git add .
git commit -m "fix: resolve merge conflict between demo branches"
```
