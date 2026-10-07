#!/usr/bin/env bash
# ============================================================
# CIRT DevOps — Automated PostgreSQL Database Backup
# Dumps cirt_db into a compressed SQL archive
# ============================================================

set -eo pipefail

BACKUP_DIR="${BACKUP_DIR:-./backups}"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/cirt_db_${TIMESTAMP}.sql.gz"
CONTAINER_NAME="${DB_CONTAINER:-cirt-db}"
DB_USER="${POSTGRES_USER:-postgres}"
DB_NAME="${POSTGRES_DB:-cirt_db}"

mkdir -p "${BACKUP_DIR}"

echo "============================================================"
echo " Starting Database Backup for ${DB_NAME}..."
echo "============================================================"

if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo "Dumping database from container '${CONTAINER_NAME}'..."
    docker exec "${CONTAINER_NAME}" pg_dump -U "${DB_USER}" "${DB_NAME}" | gzip > "${BACKUP_FILE}"
else
    echo "Notice: Container '${CONTAINER_NAME}' not running. Attempting local pg_dump..."
    pg_dump -U "${DB_USER}" -h localhost "${DB_NAME}" | gzip > "${BACKUP_FILE}"
fi

FILE_SIZE=$(du -h "${BACKUP_FILE}" | cut -f1)
echo "SUCCESS: Database backup written to ${BACKUP_FILE} (Size: ${FILE_SIZE})"
echo "============================================================"
