#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

# ------------------------------------------------------------------------------
# Configuration & Defaults
# ------------------------------------------------------------------------------
ENV_FILE="${ENV_FILE:-$PROJECT_ROOT/dev.env}"

# ------------------------------------------------------------------------------
# Pre-flight Checks
# ------------------------------------------------------------------------------
if [[ ! -f "$ENV_FILE" ]]; then
  echo "[ERROR] Environment file '$ENV_FILE' not found!" >&2
  exit 1
fi

# ------------------------------------------------------------------------------
# Environment Setup
# ------------------------------------------------------------------------------
set -a
# shellcheck disable=SC1090
source "$ENV_FILE"
set +a

# Unset DYLD_LIBRARY_PATH to prevent macOS dynamic library conflicts
unset DYLD_LIBRARY_PATH

# ------------------------------------------------------------------------------
# Build & Run
# ------------------------------------------------------------------------------
cd "$SCRIPT_DIR"

mvn -q clean install

mkdir -p dist/plugins
cp tool-hub-plugins/*/target/plugin-*.zip dist/plugins/

cd dist
exec java -jar ../tool-hub-app/target/tool-hub-app-*.jar "$@"