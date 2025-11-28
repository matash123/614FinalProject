# 614Project

# Setup
1. `cd {yourPath}/614Projects`
2. init database `sqlite3 flights.db < utils/schema.sql` for windows -> `sqlite3 mydatabase.db ".read utils/schema.sql"`
3. create ".env" file and add  "DB_PATH=flights.db" without quotations.  

# How to run
There are two Options:
1. self compile and run, last correct commands are below
   - `cd {yourPath}/614Projects`
   -  `javac -cp utils/sqlite-jdbc.jar -d out src/*.java src/**/*.java` for windows -> `javac -cp ".;utils/sqlite-jdbc.jar" -d out src/MainApp.java`
   -   `java -cp out:utils/sqlite-jdbc.jar src.MainApp` for windows -> java -cp "out;utils/sqlite-jdbc.jar" src.MainApp

3. Use the run script (only works for linux)
   - `cd {yourPath}/614Projects`
   - `chmod +x run.sh` -> only on git pulls and if you change the run file
   - `./run.sh`
 
 
Flight booking application prototype
#Marley Test
#Test Again

### actives work

#### matin
- init work on controllers

#### marley
- init work on classes / domain & database lead

#### Barrett
- Init work on the GUI


#### Raman
- Managing diagrams and overall design. 
