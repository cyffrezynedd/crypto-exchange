#!/bin/sh
set -e

PSQL="psql -v ON_ERROR_STOP=1 -U ${POSTGRES_USER:-postgres} -d ${POSTGRES_DB:-crypto_exchange}"

$PSQL <<'SQL'
CREATE TABLE IF NOT EXISTS schema_migrations (
    filename   text PRIMARY KEY,
    applied_at timestamptz NOT NULL DEFAULT now()
);
SQL

# Existing DB without history (e.g. after manual migrate) — mark all as applied
if [ "$($PSQL -tAc "SELECT COUNT(*) FROM schema_migrations")" = "0" ] \
   && [ "$($PSQL -tAc "SELECT to_regclass('public.users') IS NOT NULL")" = "t" ]; then
    echo "Baselining existing schema..."
    for f in /migrations/*.sql; do
        $PSQL -c "INSERT INTO schema_migrations (filename) VALUES ('$(basename "$f")') ON CONFLICT DO NOTHING;"
    done
    echo "Done."
    exit 0
fi

for f in /migrations/*.sql; do
    base=$(basename "$f")
    if [ "$($PSQL -tAc "SELECT 1 FROM schema_migrations WHERE filename = '$base'")" = "1" ]; then
        echo "skip $base"
        continue
    fi
    echo ">> $base"
    $PSQL -f "$f"
    $PSQL -c "INSERT INTO schema_migrations (filename) VALUES ('$base');"
done

echo "Migrations up to date."
