#!/usr/bin/env bash
# ============================================================
# CIRT DevOps — System Health Check Script
# Validates Actuator health endpoint and database connectivity
# ============================================================

set -eo pipefail

HOST="${1:-localhost}"
PORT="${2:-9090}"
HEALTH_URL="http://${HOST}:${PORT}/actuator/health"

echo "Checking CIRT Application Health at ${HEALTH_URL}..."

RESPONSE=$(curl -s "${HEALTH_URL}" || echo "")

if echo "${RESPONSE}" | grep -q '"status":"UP"'; then
    echo "HEALTH CHECK PASSED: CIRT DevOps Application is UP and HEALTHY."
    echo "Details: ${RESPONSE}"
    exit 0
else
    echo "HEALTH CHECK FAILED: Application is DOWN or unreachable."
    echo "Response: ${RESPONSE}"
    exit 1
fi
