# CIRT DevOps — Disaster Recovery & Backup Runbook

## Overview
This document outlines the backup strategy, disaster recovery procedures, and restore workflows for the Cybersecurity Incident Response Tracker (CIRT) application and PostgreSQL database.

---

## 1. Automated Backup Strategy
- **Frequency**: Daily at 02:00 UTC via cron.
- **Retention**: 30 days of daily backups stored locally and mirrored to off-site cloud storage.
- **Script Location**: `./scripts/backup_db.sh`

### Executing a Manual Backup
```bash
./scripts/backup_db.sh
```
Output backup files are saved as `./backups/cirt_db_YYYYMMDD_HHMMSS.sql.gz`.

---

## 2. Disaster Recovery & Restore Workflow

### Recovery Objectives
- **Recovery Point Objective (RPO)**: < 24 hours
- **Recovery Time Objective (RTO)**: < 15 minutes

### Restoring Database from Backup
To restore the CIRT database to a specific point-in-time backup file:

```bash
./scripts/restore_db.sh backups/cirt_db_20261007_020000.sql.gz
```

---

## 3. Full Stack Recovery Procedure
In the event of total server loss:

1. Provision new server using Ansible:
   ```bash
   ansible-playbook -i ansible/inventory.ini ansible/playbook.yml
   ```
2. Restore latest database snapshot:
   ```bash
   ./scripts/restore_db.sh <latest_backup.sql.gz>
   ```
3. Deploy application stack:
   ```bash
   ./scripts/deploy.sh
   ```
4. Verify system health:
   ```bash
   ./scripts/health_check.sh
   ```
