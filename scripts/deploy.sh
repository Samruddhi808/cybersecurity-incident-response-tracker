#!/usr/bin/env bash
# ============================================================
# CIRT DevOps — Automated Deployment Script
# Usage: ./scripts/deploy.sh [IMAGE_TAG]
# ============================================================

set -eo pipefail

IMAGE_TAG="${1:-latest}"
APP_DIR="${APP_DIR:-.}"
HEALTH_URL="http://localhost:9090/actuator/health"
MAX_RETRIES=12
SLEEP_SECS=5

echo "============================================================"
echo " Starting CIRT DevOps Deployment (Tag: ${IMAGE_TAG})"
echo "============================================================"

# Navigate to application directory
cd "${APP_DIR}"

# Step 1: Backup current environment state
echo "[1/4] Recording current running version for rollback safety..."
CURRENT_CONTAINER=$(docker inspect --format='{{.Image}}' cirt-app 2>/dev/null || echo "")
echo "Current image ID: ${CURRENT_CONTAINER:-None}"

# Step 2: Build or pull latest images
echo "[2/4] Building container stack..."
IMAGE_TAG="${IMAGE_TAG}" docker compose build --no-cache

# Step 3: Deploy containers with zero extended downtime
echo "[3/4] Launching container stack..."
IMAGE_TAG="${IMAGE_TAG}" docker compose up -d

# Step 4: Health Check Verification
echo "[4/4] Verifying application health endpoint at ${HEALTH_URL}..."
HEALTHY=false
for i in $(seq 1 ${MAX_RETRIES}); do
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "${HEALTH_URL}" || echo "000")
    if [ "${HTTP_CODE}" -eq 200 ]; then
        echo "SUCCESS: Health check returned HTTP 200 OK!"
        HEALTHY=true
        break
    fi
    echo "Waiting for application startup... (Attempt ${i}/${MAX_RETRIES}, HTTP: ${HTTP_CODE})"
    sleep ${SLEEP_SECS}
done

if [ "${HEALTHY}" = true ]; then
    echo "============================================================"
    echo " CIRT DevOps Deployment COMPLETED SUCCESSFULLY!"
    echo "============================================================"
    exit 0
else
    echo "ERROR: Application health check failed!"
    echo "Initiating automatic rollback..."
    ./scripts/rollback.sh
    exit 1
fi
