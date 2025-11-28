#!/usr/bin/env bash

ROOT_DIR=$(cd "$(dirname "$0")"; pwd)
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
rm  "$OUT"
mkdir "$OUT"

# Compile all top-level and nested source files
javac -cp "$JAR" -d "$OUT" $SRC/*.java $SRC/**/*.java

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
