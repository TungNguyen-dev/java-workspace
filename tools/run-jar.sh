#!/usr/bin/env bash
# Usage: tools/run-jar.sh [-l] <jar-name[.jar]|path/to/file.jar> [java/app args...]
# Loads the env file first, then runs the jar under caffeinate so the Mac does not sleep mid-run.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
BIN_DIR="${BIN_DIR:-$SCRIPT_DIR/bin}"
ENV_FILE="${ENV_FILE:-$PROJECT_ROOT/dev.env}"

list_jars() {
  echo "Jars in $BIN_DIR:" >&2
  find "$BIN_DIR" -maxdepth 1 -name '*.jar' -exec basename {} \; | sort | sed 's/^/  /' >&2
}

usage() {
  echo "Usage: $(basename "$0") <jar-name[.jar]|path/to/file.jar> [args...]" >&2
  echo "       ENV_FILE=path/to/env  overrides the env file (default: $ENV_FILE)" >&2
  list_jars
  exit 1
}

[[ $# -ge 1 ]] || usage
if [[ "$1" == "-l" || "$1" == "--list" ]]; then
  list_jars
  exit 0
fi

JAR_ARG="$1"
shift

if [[ -f "$JAR_ARG" ]]; then
  JAR="$JAR_ARG"
elif [[ -f "$BIN_DIR/$JAR_ARG" ]]; then
  JAR="$BIN_DIR/$JAR_ARG"
elif [[ -f "$BIN_DIR/$JAR_ARG.jar" ]]; then
  JAR="$BIN_DIR/$JAR_ARG.jar"
else
  echo "[ERROR] Jar '$JAR_ARG' not found (looked in $BIN_DIR)" >&2
  list_jars
  exit 1
fi

if [[ ! -f "$ENV_FILE" ]]; then
  echo "[ERROR] Environment file '$ENV_FILE' not found!" >&2
  exit 1
fi

set -a
# shellcheck disable=SC1090
source "$ENV_FILE"
set +a

# DYLD_LIBRARY_PATH crashes Chrome spawned by ChromeDriver on macOS
unset DYLD_LIBRARY_PATH

echo "[INFO] env: $ENV_FILE | jar: $JAR" >&2

if command -v caffeinate >/dev/null 2>&1; then
  exec caffeinate -i java -jar "$JAR" "$@"
fi
echo "[WARN] caffeinate not found, running without it" >&2
exec java -jar "$JAR" "$@"
