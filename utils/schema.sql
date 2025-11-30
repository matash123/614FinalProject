-- schema.sql
-- Full reset + schema + test data
--
-- Run with:
--   sqlite3 flights.db < utils/schema.sql

PRAGMA foreign_keys = OFF;

DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS promotion;
DROP TABLE IF EXISTS sysadmin;
DROP TABLE IF EXISTS agent;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS flight;
DROP TABLE IF EXISTS airline;
DROP TABLE IF EXISTS airplane; -- added airplane now.
DROP TABLE IF EXISTS user;

PRAGMA foreign_keys = ON;

--------------------------------------------------
-- TABLES
--------------------------------------------------

-- Users (base for Customer, Agent, SystemAdministrator)
CREATE TABLE user (
    id       TEXT PRIMARY KEY NOT NULL,                    -- ↔ User.userId
    name     TEXT NOT NULL,                                -- ↔ User.name
    username TEXT NOT NULL,                                -- used for login
    email    TEXT,                                         -- ↔ User.email (optional for now)
    password TEXT NOT NULL,                                -- ↔ User.passwordHash
    active   INTEGER NOT NULL DEFAULT 1 CHECK(active IN (0,1)), -- ↔ User.active
    role     TEXT NOT NULL CHECK(role IN ('customer', 'agent', 'admin'))
);

-- 
CREATE TABLE customer (
    id TEXT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES user(id)
);

CREATE TABLE agent (
    id TEXT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES user(id)
);

CREATE TABLE sysadmin (
    id TEXT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES user(id)
);

-- Airlines
CREATE TABLE airline (
    airline_id TEXT PRIMARY KEY,    -- ↔ Airline.airlineId
    name       TEXT                 -- ↔ Airline.name
);

-- Flights
CREATE TABLE flight (
    flight_id        TEXT PRIMARY KEY,     -- ↔ Flight.flightId
    airplane_id      TEXT NOT NULL,         -- Flight.airplane.airplaneID
    airline_id       TEXT NOT NULL,        -- ↔ Flight.airline.airlineId
    origin           TEXT NOT NULL,        -- ↔ Flight.origin  (or departure)
    destination      TEXT NOT NULL,        -- ↔ Flight.destination
    date             TEXT NOT NULL,        -- ↔ Flight.date (YYYY-MM-DD)
    total_seats      INTEGER NOT NULL,     -- ↔ Flight.totalSeats
    available_seats  INTEGER NOT NULL,     -- ↔ Flight.availableSeats
    price            REAL NOT NULL,        -- ↔ Flight.price
    FOREIGN KEY (airline_id) REFERENCES airline(airline_id)
);

-- Reservations / bookings
CREATE TABLE reservation (
    reservation_id TEXT PRIMARY KEY,   -- ↔ Reservation.reservationId
    customer_id    TEXT NOT NULL,      -- ↔ Reservation.customer.userId
    flight_id      TEXT NOT NULL,      -- ↔ Reservation.flight.flightId
    seats          INTEGER NOT NULL,   -- ↔ Reservation.seats
    status         TEXT NOT NULL,      -- ↔ Reservation.status (enum or text)
    booking_time   TEXT,               -- ↔ Reservation.bookingDateTime
    FOREIGN KEY (customer_id) REFERENCES user(id),
    FOREIGN KEY (flight_id)   REFERENCES flight(flight_id)
);

-- Payments
CREATE TABLE payment (
    payment_id     TEXT PRIMARY KEY,   -- ↔ Payment.paymentId
    reservation_id TEXT NOT NULL,      -- ↔ Payment.reservation.reservationId
    amount         REAL NOT NULL,      -- ↔ Payment.amount
    status         TEXT NOT NULL,      -- ↔ Payment.status (enum)
    timestamp      TEXT,               -- ↔ Payment.timestamp
    FOREIGN KEY (reservation_id) REFERENCES reservation(reservation_id)
);

-- Promotions (monthly news)
CREATE TABLE promotion (
    promotion_id TEXT PRIMARY KEY,     -- ↔ Promotion.promotionId
    title        TEXT NOT NULL,        -- ↔ Promotion.title
    message      TEXT NOT NULL,        -- ↔ Promotion.message
    start_date   TEXT NOT NULL,        -- ↔ Promotion.startDate
    end_date     TEXT NOT NULL         -- ↔ Promotion.endDate
);

CREATE TABLE airplane (
    airplane_id  TEXT PRIMARY KEY,
    airline_id   TEXT NOT NULL, 
    model        TEXT NOT NULL,
    manufacturer TEXT NOT NULL,
    capacity     INTEGER NOT NULL
);

--------------------------------------------------
-- TEST DATA
--------------------------------------------------

-- Users
INSERT INTO user (id, name, username, email, password, active, role) VALUES 
('0','timmy',  'kingTimmy',   'timmy@example.com',  '1', 1, 'customer'),
('1','jeff',   'kingJeff',    'jeff@example.com',   '1', 1, 'customer'),
('2','sarah',  'queenSarah',  'sarah@example.com',  '1', 1, 'agent'),
('3','robot',  'kingRobot',   'robot@example.com',  '1', 1, 'agent'),
('4','sam',    'queenSam',    'sam@example.com',    '1', 1, 'admin'),
('5','leroy',  'leroyJenkins','leroy@example.com',  '1', 1, 'admin');

-- Subtype rows (optional but nice)
INSERT INTO customer (id) VALUES ('0'), ('1');
INSERT INTO agent    (id) VALUES ('2'), ('3');
INSERT INTO sysadmin (id) VALUES ('4'), ('5');

-- Airlines
INSERT INTO airline (airline_id, name) VALUES
('WSJ', 'WestJet'),
('ACN', 'Air Canada'),
('FLR', 'Flair');

-- Airplane

INSERT INTO airplane (airplane_id, airline_id, model, manufacturer, capacity) VALUES
('WSJ-737-1', 'WSJ', 'Boeing 737-800', 'Boeing', 160),
('ACN-320-1', 'ACN', 'Airbus A320',    'Airbus', 180),
('FLR-737-1', 'FLR', 'Boeing 737-700', 'Boeing', 140);


-- Flights
INSERT INTO flight (flight_id, airline_id, airplane_id, origin, destination, date, total_seats, available_seats, price) VALUES
('FL100', 'WSJ', 'WSJ-737-1', 'Calgary',   'Vancouver', '2025-12-01', 160, 120, 199.99),
('FL200', 'ACN', 'ACN-320-1', 'Calgary',   'Toronto',   '2025-12-05', 180,  50, 249.50),
('FL300', 'FLR', 'FLR-737-1', 'Edmonton',  'Vancouver', '2025-12-10', 140, 140, 149.99),
('FL400', 'FLR', 'FLR-737-1', 'Edmonton',  'Vancouver', '2024-11-02', 140, 140, 149.99);

-- Reservations
INSERT INTO reservation (reservation_id, customer_id, flight_id, seats, status, booking_time) VALUES
('R001', '1', 'FL100', 1, 'booked',   '2025-11-01T10:00:00'),
('R002', '1', 'FL200', 2, 'booked',   '2025-11-02T11:30:00'),
('R003', '2', 'FL300', 1, 'cancelled','2025-11-03T09:15:00');

-- Payments
INSERT INTO payment (payment_id, reservation_id, amount, status, timestamp) VALUES
('P001', 'R001', 199.99, 'paid',  '2025-11-01T10:05:00'),
('P002', 'R002', 249.50, 'paid',  '2025-11-02T11:35:00');

-- Promotions
INSERT INTO promotion (promotion_id, title, message, start_date, end_date) VALUES
('PR001', 'Winter Sale', '20% off selected flights', '2025-11-01', '2025-12-31');



