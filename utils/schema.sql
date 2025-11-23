-- 

-- schema_with_testdata.sql
-- Full reset + schema + test data

-- LOAD WITH -------
-- sqlite3 flights.db < utils/schema.sql    -- 
--------------------


PRAGMA foreign_keys = OFF;

DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS flight;
DROP TABLE IF EXISTS airline;
DROP TABLE IF EXISTS ariline_flight;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS sysadmin;
DROP TABLE IF EXISTS agent;
DROP TABLE IF EXISTS customer;

PRAGMA foreign_keys = ON;

--------------------------------------------------
-- TABLES
--------------------------------------------------

CREATE TABLE flight (
    flight_id TEXT PRIMARY KEY,
    airline_id TEXT NOT NULL,
    origin TEXT NOT NULL,
    destination TEXT NOT NULL,
    date TEXT NOT NULL,
    seats INTEGER NOT NULL,
    price REAL NOT NULL,
    FOREIGN KEY (airline_id) REFERENCES airline(airline_id)

    
);

CREATE TABLE user (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL, 
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    role TEXT NOT NULL CHECK(role IN ('customer', 'agent', 'admin'))
);

CREATE TABLE customer (
    id TEXT NOT NULL,
    FOREIGN KEY (id) REFERENCES user(id)
);

CREATE TABLE agent (
    id TEXT NOT NULL,
    FOREIGN KEY (id) REFERENCES user(id)
);

CREATE TABLE sysadmin (
    id TEXT NOT NULL,
    FOREIGN KEY (id) REFERENCES user(id)
);

CREATE TABLE reservation (
    reservation_id TEXT PRIMARY KEY,
    customer_id TEXT NOT NULL,
    flight_id TEXT NOT NULL,
    status TEXT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES user(id),
    FOREIGN KEY (flight_id) REFERENCES flight(flight_id)
);

CREATE TABLE payment (
    payment_id TEXT PRIMARY KEY,
    reservation_id TEXT NOT NULL,
    amount REAL NOT NULL,
    status TEXT NOT NULL,
    FOREIGN KEY (reservation_id) REFERENCES reservation(reservation_id)
);

CREATE TABLE airline (
    airline_id TEXT PRIMARY KEY
);



--------------------------------------------------
-- TEST DATA
--------------------------------------------------

-- Users
INSERT INTO user VALUES 
('0','timmy', 'kingTimmy', '1', 'customer'),
('1','jeff', 'kingJeff', '1', 'customer'),
('2','sarah', 'queenSarah', '1', 'agent'),
('3','robot', 'kingRobot', '1', 'agent'),
('4','sam', 'queenSam', '1', 'admin'),
('5','leroy', 'leroyJenkins', '1', 'admin');

-- airlines
INSERT INTO airline VALUES
('WSJ'),
('ACN'),
('FLR');

-- Flights
INSERT INTO flight VALUES
('FL100', 'WSJ','Calgary',     'Vancouver',  '2025-12-01', 120, 199.99),
('FL200', 'WSJ','Toronto',     'New York',   '2025-12-05', 180, 249.50),
('FL300', 'ACN','Montreal',    'Chicago',    '2025-12-10', 150, 220.00),
('FL400', 'FLR','Edmonton',    'Calgary',    '2025-12-15',  80, 129.99);


-- Reservations
INSERT INTO reservation VALUES
('R001', '1', 'FL100', 'booked'),
('R002', '1', 'FL200', 'booked'),
('R003', '2', 'FL300', 'cancelled');

-- Payments
INSERT INTO payment VALUES
('P001', 'R001', 199.99, 'paid'),
('P002', 'R002', 249.50, 'paid');




