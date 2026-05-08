#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$ROOT_DIR/backend"
FRONTEND_DIR="$ROOT_DIR/frontend"
JAVA_HOME_DEFAULT="/usr/lib/jvm/java-17-openjdk-amd64"
BACKEND_PID_FILE="$ROOT_DIR/.syndicare-backend.pid"
FRONTEND_PID_FILE="$ROOT_DIR/.syndicare-frontend.pid"

terminate() {
  if [[ -n "${BACKEND_PID:-}" ]]; then
    kill -- "-$BACKEND_PID" 2>/dev/null || true
  fi
  if [[ -n "${FRONTEND_PID:-}" ]]; then
    kill -- "-$FRONTEND_PID" 2>/dev/null || true
  fi
  rm -f "$BACKEND_PID_FILE" "$FRONTEND_PID_FILE"
}

trap terminate EXIT INT TERM

if [[ -d "$FRONTEND_DIR/node_modules" ]]; then
  setsid bash -lc "cd '$FRONTEND_DIR' && npm run dev" &
else
  setsid bash -lc "cd '$FRONTEND_DIR' && npm install && npm run dev" &
fi
FRONTEND_PID=$!
echo "$FRONTEND_PID" > "$FRONTEND_PID_FILE"

setsid bash -lc "cd '$BACKEND_DIR' && JAVA_HOME='${JAVA_HOME:-$JAVA_HOME_DEFAULT}' mvn spring-boot:run" &
BACKEND_PID=$!
echo "$BACKEND_PID" > "$BACKEND_PID_FILE"

wait "$BACKEND_PID" "$FRONTEND_PID"
