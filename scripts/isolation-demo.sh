#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

# Two real PostgreSQL sessions; only this run's disposable table is changed.
DB_NAME="${DB_NAME:-school_liquibase_db}"
DB_USER="${DB_USER:-postgres}"
table="isolation_demo_$$_${RANDOM}"
tmp_dir="$(mktemp -d)"
reader_pid=""
sql() { docker compose exec -T postgres psql -X -v ON_ERROR_STOP=1 -U "$DB_USER" -d "$DB_NAME" "$@"; }
cleanup() {
  if [[ -n "$reader_pid" ]]; then
    kill "$reader_pid" 2>/dev/null || true
    wait "$reader_pid" 2>/dev/null || true
  fi
  sql -qc "DROP TABLE IF EXISTS $table" || true
  rm -f "$tmp_dir/reader.txt"
  rmdir "$tmp_dir"
}
trap cleanup EXIT
sql -qc "CREATE TABLE $table (id integer PRIMARY KEY)"

for level in 'READ COMMITTED' 'REPEATABLE READ' 'SERIALIZABLE'; do
  sql -qc "TRUNCATE TABLE $table"
  app="${table}_reader"
  sql -At >"$tmp_dir/reader.txt" <<SQL &
SET application_name = '$app';
BEGIN ISOLATION LEVEL $level READ ONLY;
SELECT count(*) FROM $table;
SELECT pg_sleep(10);
SELECT count(*) FROM $table;
COMMIT;
SQL
  reader_pid=$!
  ready=false
  for ((attempt=0; attempt<80; attempt++)); do
    if [[ "$(sql -Atqc "SELECT count(*) FROM pg_stat_activity WHERE application_name = '$app' AND wait_event = 'PgSleep'")" == 1 ]]; then
      ready=true
      break
    fi
    sleep 0.1
  done
  if [[ "$ready" != true ]]; then
    echo 'Reader did not reach the synchronization point.' >&2
    exit 1
  fi
  # Session B commits while session A waits after its first SELECT.
  sql -qc "INSERT INTO $table VALUES (1)"
  wait "$reader_pid"
  reader_pid=""
  counts="$(grep -E '^[0-9]+$' "$tmp_dir/reader.txt" | tr '\n' ' ' | sed 's/ $//')"
  expected='0 0'
  [[ "$level" != 'READ COMMITTED' ]] || expected='0 1'
  printf '%s: counts=[%s], expected=[%s]\n' "$level" "$counts" "$expected"
  [[ "$counts" == "$expected" ]] || exit 1
done
