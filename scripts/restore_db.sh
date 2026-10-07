#!/usr/bin/env bash
# ============================================================
# CIRT DevOps — Database Disaster Recovery & Restore
# Usage: ./scripts/restore_db.sh <path_to_backup.sql.gz>
# ============================================================

set -eo pipefail

BACKUP_FILE="$1"
CONTAINER_NAME="${DB_CONTAINER:-cirt-db}"
DB_USER="${POSTGRES_USER:-postgres}"
DB_NAME="${POSTGRES_DB:-cirt_db}"

if [ -z "${BACKUP_FILE}" ] || [ ! -f "${BACKUP_FILE}" ]; then
    echo "ERROR: Please specify a valid backup file (.sql.gz) to restore."
    echo "Usage: ./scripts/restore_db.sh backups/cirt_db_YYYYMMDD_HHMMSS.sql.gz"
    exit 1
fi

echo "============================================================"
echo " Restoring Database ${DB_NAME} from ${BACKUP_FILE}..."
echo "============================================================"

if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo "Restoring database in container '${CONTAINER_NAME}'..."
    gunzip -c "${BACKUP_FILE}" | docker exec -i "${CONTAINER_NAME}" psql -U "${DB_USER}" -d "${DB_NAME}"
else
    echo "Restoring local database..."
    gunzip -c "${BACKUP_FILE}" | psql -U "${DB_USER}" -h localhost -d "${DB_NAME}"
fi

echo "SUCCESS: Database restore operation finished!"
echo "============================================================"
