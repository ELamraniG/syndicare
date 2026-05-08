#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_PID_FILE="$ROOT_DIR/.syndicare-backend.pid"
FRONTEND_PID_FILE="$ROOT_DIR/.syndicare-frontend.pid"

stop_group() {
  local pid_file="$1"
  if [[ ! -f "$pid_file" ]]; then
    return 0
  fi

  local pid
  pid="$(cat "$pid_file")"
  if [[ -n "$pid" ]]; then
    kill -- "-$pid" 2>/dev/null || true
  fi
  rm -f "$pid_file"
}

stop_port() {
  local port="$1"
  local pids
  pids="$(lsof -ti tcp:"$port" -sTCP:LISTEN 2>/dev/null || true)"
  if [[ -n "$pids" ]]; then
    kill $pids 2>/dev/null || true
  fi
}

stop_group "$BACKEND_PID_FILE"
stop_group "$FRONTEND_PID_FILE"
stop_port 8080
stop_port 5173
stop_port 5174
stop_port 5175