# 614Project

# Setup
1. `cd {yourPath}/614Projects`
2. init database `sqlite3 flights.db < utils/schema.sql`
3. create ".env" file and add  "DB_PATH=flights.db" without quotations.  

# How to run
There are two Options:
1. self compile and run, last correct commans are below 
 1.1 `cd {yourPath}/614Projects`
 1.2 `javac -cp utils/sqlite-jdbc.jar -d out src/*.java src/**/*.java`
 1.3 `java -cp out:utils/sqlite-jdbc.jar src.MainApp`

2. Use the run script
 2.1 `cd {yourPath}/614Projects`
 2.2 `chmod +x run.sh` -> only on git pulls and if you change the run file
 2.3 `./run.sh`
 
 
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
