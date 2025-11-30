#!/usr/bin/env bash

ROOT_DIR=$(cd "$(dirname "$0")"; pwd)
JAR="$ROOT_DIR/utils/sqlite-jdbc.jar"
SRC="$ROOT_DIR/src"
OUT="$ROOT_DIR/out"
ENV_FILE="$ROOT_DIR/.env"
DB_FILE="$ROOT_DIR/flights.db"
SCHEMA_FILE="$ROOT_DIR/utils/schema.sql"

# Check .env
if [ ! -f "$ENV_FILE" ]; then
    echo ".env not found in project root. Abort."
    exit 1
fi

# Simple database init (only if missing)
if [ ! -f "$DB_FILE" ]; then
    echo "flights.db not found – initializing from utils/schema.sql"

    if [ ! -f "$SCHEMA_FILE" ]; then
        echo "Schema file utils/schema.sql not found. Cannot initialize database."
        exit 1
    fi

    if ! command -v sqlite3 >/dev/null 2>&1; then
        echo "sqlite3 command not found. Please install SQLite or create flights.db manually."
        exit 1
    fi

    sqlite3 "$DB_FILE" < "$SCHEMA_FILE" || {
        echo "Failed to initialize flights.db"
        exit 1
    }
fi

# Ensure output directory exists (no deletion)
mkdir -p "$OUT"

# Compile all Java sources under src
javac -cp "$JAR" -d "$OUT" $(find "$SRC" -name '*.java')

if [ $? -ne 0 ]; then
    echo "Compile failed."
    exit 1
fi

# Determine classpath separator based on OS
SEP=":"
case "$OSTYPE" in
    msys*|cygwin*) SEP=";" ;;  # Windows Git Bash
esac

# Run
java -cp "$OUT$SEP$JAR" src.MainApp
