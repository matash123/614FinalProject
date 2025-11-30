#!/usr/bin/env pwsh

# --- Resolve project root ---
$ROOT_DIR = Split-Path -Parent $MyInvocation.MyCommand.Definition
$JAR      = Join-Path $ROOT_DIR "utils/sqlite-jdbc.jar"
$SRC      = Join-Path $ROOT_DIR "src"
$OUT      = Join-Path $ROOT_DIR "out"
$ENV_FILE = Join-Path $ROOT_DIR ".env"
$DB_FILE  = Join-Path $ROOT_DIR "flights.db"
$SCHEMA   = Join-Path $ROOT_DIR "utils/schema.sql"

# --- Populate DB (always runs first) ---
#sqlite3 $DB_FILE ".read $SCHEMA"

# --- Check .env ---
if (!(Test-Path $ENV_FILE)) {
    Write-Host ".env not found in project root. Abort."
    exit 1
}

# --- Check flights.db ---
if (!(Test-Path $DB_FILE)) {
    Write-Host "flights.db not found in project root. Abort."
    exit 1
}

# --- Clean + recompile ---
if (Test-Path $OUT) { Remove-Item $OUT -Recurse -Force }
New-Item -ItemType Directory -Path $OUT | Out-Null

# Recursively get all .java files
$javaFiles = Get-ChildItem -Path $SRC -Recurse -Filter *.java | ForEach-Object { $_.FullName }

# Compile
javac -cp "$JAR" -d "$OUT" $javaFiles
if ($LASTEXITCODE -ne 0) {
    Write-Host "Compile failed."
    exit 1
}

# Windows classpath separator
$SEP = ";"

# --- Run program ---
java -cp "$OUT$SEP$JAR" src.MainApp
