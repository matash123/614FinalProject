# ENSF 614 Flight Booking Application

## Overview

This repository contains a Java desktop prototype for booking and managing flights. It was built as a team project for a software engineering course.

## Repository

* GitHub profile: https://github.com/matash123
* Project repository: https://github.com/matash123/614FinalProject
* Original shared repository: https://github.com/barrettsapunjis/614Project

## What this project includes

* A Java desktop application prototype.
* A SQLite database setup script in `utils/schema.sql`.
* Demo login accounts for the admin and agent roles.
* Controller work listed for Matin in the team README.

## Team contribution listed in the README

* Matin worked on controllers.
* Marley worked on domain classes and the database.
* Barrett worked on the GUI.
* Raman worked on diagrams and overall design.

## Setup

From the project folder, initialize the database.

```bash
cd 614FinalProject
sqlite3 flights.db < utils/schema.sql
```

On Windows, you can use this SQLite command instead.

```powershell
sqlite3 mydatabase.db ".read utils/schema.sql"
```

Create a `.env` file in the project root with this value.

```text
DB_PATH=flights.db
```

## Run the application

You can compile and run the project manually.

```bash
javac -cp utils/sqlite-jdbc.jar -d out src/*.java src/**/*.java
java -cp out:utils/sqlite-jdbc.jar src.MainApp
```

On Windows, use this command format.

```powershell
javac -cp ".;utils/sqlite-jdbc.jar" -d out src/MainApp.java
java -cp "out;utils/sqlite-jdbc.jar" src.MainApp
```

You can also run the Linux script.

```bash
chmod +x run-linux.sh
./run-linux.sh
```

You can also run the Windows script.

```powershell
./run-win.ps1
```

## Demo login

These are test credentials for the prototype. They are not production accounts.

```text
Admin username: admin
Admin password: 1

Agent username: agent
Agent password: 1
```

## Requirements

* Java JDK.
* SQLite.
* SQLite JDBC jar file in the `utils` folder.
