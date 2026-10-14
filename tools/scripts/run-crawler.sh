#!/usr/bin/env bash

# Enable strict mode: exit immediately if a command fails, an unset variable is used,
# or a command in a pipeline fails.
set -euo pipefail

# ------------------------------------------------------------------------------
# Configuration & Defaults
# ------------------------------------------------------------------------------
ENV_FILE="${ENV_FILE:-dev.env}"

# Generate dynamic timestamp (Format: YYYYMMDD-HHMMSS, e.g., 20261002-001251)
TIMESTAMP="$(date +'%Y%m%d-%H%M%S')"
DEFAULT_LOG_FILE="storage/logs/crawler/run-crawler-${TIMESTAMP}.log"

LOG_FILE="${LOG_FILE:-$DEFAULT_LOG_FILE}"
JAR_FILE="${JAR_FILE:-tools/bin/crawler-tool-app-0.0.1-SNAPSHOT.jar}"

# ------------------------------------------------------------------------------
# Pre-flight Checks
# ------------------------------------------------------------------------------

# Ensure the environment file exists
if [[ ! -f "$ENV_FILE" ]]; then
  echo "[ERROR] Environment file '$ENV_FILE' not found!" >&2
  exit 1
fi

# Ensure the target JAR application exists
if [[ ! -f "$JAR_FILE" ]]; then
  echo "[ERROR] Executable JAR file '$JAR_FILE' not found!" >&2
  exit 1
fi

# Create destination directory for logs if it does not exist
mkdir -p "$(dirname "$LOG_FILE")"

# ------------------------------------------------------------------------------
# Environment Setup
# ------------------------------------------------------------------------------

# Automatically export all variables defined in the .env file
set -a
# shellcheck disable=SC1090
source "$ENV_FILE"
set +a

# Unset DYLD_LIBRARY_PATH to prevent macOS dynamic library conflicts
# that crash the Google Chrome binary spawned by ChromeDriver.
unset DYLD_LIBRARY_PATH

# ------------------------------------------------------------------------------
# Execution
# ------------------------------------------------------------------------------

echo "[INFO] Starting Crawler Application..."
echo "[INFO] Logging output to: $LOG_FILE"

# Run Java application using caffeinate to prevent macOS system sleep during long runs
caffeinate -i java -jar "$JAR_FILE" 2>&1 | tee -a "$LOG_FILE"