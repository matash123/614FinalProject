-- 

-- schema_with_testdata.sql
-- Full reset + schema + test data

-- LOAD WITH -------
-- sqlite3 flights.db
-- .read schema_with_testdata.sql
-- .tables
--------------------


PRAGMA foreign_keys = OFF;

DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS flight;

PRAGMA foreign_keys = ON;

--------------------------------------------------
-- TABLES
--------------------------------------------------

CREATE TABLE flight (
    flight_id TEXT PRIMARY KEY,
    origin TEXT NOT NULL,
    destination TEXT NOT NULL,
    date TEXT NOT NULL,
    seats INTEGER NOT NULL,
    price REAL NOT NULL
);

CREATE TABLE customer (
    customer_id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL
);

CREATE TABLE reservation (
    reservation_id TEXT PRIMARY KEY,
    customer_id TEXT NOT NULL,
    flight_id TEXT NOT NULL,
    status TEXT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
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
)

CREATE TABLE ariline_flight (
    flight_id TEXT FOREIGN KEY (flight_id) REFERENCES flight(flight_id)
    airline_id TEXT FOREIGN KEY (airline_id) REFERENCES airline_id(airline_id)
)



--------------------------------------------------
-- TEST DATA
--------------------------------------------------

-- Flights
INSERT INTO flight VALUES
('FL100', 'Calgary',     'Vancouver',  '2025-12-01', 120, 199.99),
('FL200', 'Toronto',     'New York',   '2025-12-05', 180, 249.50),
('FL300', 'Montreal',    'Chicago',    '2025-12-10', 150, 220.00),
('FL400', 'Edmonton',    'Calgary',    '2025-12-15',  80, 129.99);

-- Customers
INSERT INTO customer VALUES
('C001', 'John Smith',        'john@example.com'),
('C002', 'Alice Doe',         'alice@example.com'),
('C003', 'Michael Johnson',   'mjohnson@example.com');

-- Reservations
INSERT INTO reservation VALUES
('R001', 'C001', 'FL100', 'booked'),
('R002', 'C002', 'FL200', 'booked'),
('R003', 'C003', 'FL300', 'cancelled');

-- Payments
INSERT INTO payment VALUES
('P001', 'R001', 199.99, 'paid'),
('P002', 'R002', 249.50, 'paid');

INSERT INTO airline VALUES
('WSJT'),
('AIRCN'),
('FLR');

INSERT INTO ariline_flight VALUES
('WSJT', 'FL100'),
('AIRCN', 'FL200'),
('FLR', 'FL300');
