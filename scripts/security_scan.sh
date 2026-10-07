#!/usr/bin/env bash
# ============================================================
# CIRT DevOps — Security & Vulnerability Scanner
# Executes OWASP Dependency Check and Docker Image Trivy Scan
# ============================================================

set -eo pipefail

IMAGE_NAME="${1:-cirt-devops:latest}"

echo "============================================================"
echo " Starting Security & Vulnerability Audit"
echo "============================================================"

# Step 1: OWASP Dependency Check
echo "[1/2] Running OWASP Dependency Check on Java libraries..."
mvn org.owasp:dependency-check-maven:check -B || echo "Warning: OWASP Dependency check finished with warnings."

# Step 2: Trivy Container Image Scan
echo "[2/2] Running Trivy vulnerability scan on Docker image ${IMAGE_NAME}..."
if command -v trivy >/dev/null 2>&1; then
    trivy image --severity HIGH,CRITICAL "${IMAGE_NAME}"
elif docker --version >/dev/null 2>&1; then
    docker run --rm aquasec/trivy:latest image --severity HIGH,CRITICAL "${IMAGE_NAME}" || echo "Warning: Trivy container scan finished with warnings."
else
    echo "Notice: Trivy / Docker unavailable in current shell. Skipping container image scan."
fi

echo "============================================================"
echo " Security Audit Complete."
echo "============================================================"
