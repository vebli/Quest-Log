#!/usr/bin/env bash
set -e

DB_FILE="db.sqlite3"
SCHEMA_FILE="sql/schema.sql"

rm -f "$DB_FILE" 

sqlite3 "$DB_FILE" < "$SCHEMA_FILE"

echo "db created at $DB_FILE."
