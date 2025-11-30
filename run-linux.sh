#!/usr/bin/env bash

ROOT_DIR=$(cd "$(dirname "$0")" && pwd)
JAR="$ROOT_DIR/utils/sqlite-jdbc.jar"
SRC="$ROOT_DIR/src"
OUT="$ROOT_DIR/out"
ENV_FILE="$ROOT_DIR/.env"
DB_FILE="$ROOT_DIR/flights.db"

# Check .env
if [ ! -f "$ENV_FILE" ]; then
    echo ".env not found in project root. Abort."
    exit 1
fi

# Check flights.db
if [ ! -f "$DB_FILE" ]; then
    echo "flights.db not found in project root. Abort."
    exit 1
fi

# Clean + recompile
rm -rf "$OUT"
mkdir -p "$OUT"

echo "Finding Java source files..."
cd "$SRC"
find . -name "*.java" > "$OUT/sources.txt"

echo "Compiling..."
# IMPORTANT: stay inside $SRC so ./database/... etc are valid paths
javac -cp "$JAR" -d "$OUT" @"$OUT/sources.txt"

if [ $? -ne 0 ]; then
    echo "Compile failed."
    exit 1
fi

echo "Running app..."
# go back to project root just for neatness
cd "$ROOT_DIR"
java -cp "$OUT:$JAR" src.MainApp
