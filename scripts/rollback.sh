#!/usr/bin/env bash
# ============================================================
# CIRT DevOps — Deployment Rollback Script
# Reverts container stack to previous state if deployment fails
# ============================================================

set -eo pipefail

APP_DIR="${APP_DIR:-.}"

echo "============================================================"
echo " Initiating Emergency Rollback for CIRT DevOps Stack"
echo "============================================================"

cd "${APP_DIR}"

echo "[1/2] Stopping failed container deployment..."
docker compose down || true

echo "[2/2] Restoring previous known-good deployment..."
if docker image inspect cirt-devops:previous >/dev/null 2>&1; then
    docker tag cirt-devops:previous cirt-devops:latest
    docker compose up -d
    echo "Rollback to previous container image succeeded!"
else
    echo "Notice: No 'previous' image tag found. Restarting standard stack..."
    docker compose up -d
fi

echo "============================================================"
echo " Rollback execution complete."
echo "============================================================"
