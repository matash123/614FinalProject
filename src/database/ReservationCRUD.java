package src.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import src.models.Flight;
import src.models.Reservation;
import src.models.ReservationStatus;
import src.models.User;

public class ReservationCRUD {

    /**
     * Insert a new reservation row.
     * We only store the link to customer + flight (via IDs).
     */
    public static void saveReservation(Reservation r) {
        System.out.println("In saving function, saving reservation: " + r.getReservationId());
        if (r == null) {
            throw new IllegalArgumentException("reservation cannot be null");
        }
        if (r.getUser() == null || r.getFlight() == null) {
            throw new IllegalArgumentException("user and flight are required on reservation");
        }

        try {
            String sql =
                "INSERT INTO reservation (" +
                "  reservation_id, customer_id, flight_id, seats, status, booking_time" +
                ") VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = DB.prepare(sql);
            
            DB.set(stmt, 1, r.getReservationId());
            DB.set(stmt, 2, r.getUser().getUserId());
            DB.set(stmt, 3, r.getFlight().getFlightId());
            // seats is an int on the model, but our DB helper stores strings
            DB.set(stmt, 4, Integer.toString(r.getSeats()));
            DB.set(stmt, 5, r.getStatus().name()); // we store enum name (CREATED, CONFIRMED, ...)

            LocalDateTime ts = r.getBookingDateTime();
            DB.set(stmt, 6, ts != null ? ts.toString() : null);
            
            System.out.println("reservation stmt: " + stmt);
            DB.update(stmt);

        } catch (RuntimeException e) {
            System.err.println("Error saving reservation: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Load all reservations for a given user (customer_id).
     *
     * key is we dont rebuild airline/airplane manually here. I have detailed explanation under the findbyflightid in FlightCRUD and reasoning above function
     * please refere there
     *
     * 
     * Instead by intentional design flight_id and let FlightCrud.findFlightByID (ID) construct the Flight object that up to date with the database
     * gonna be key for tracking people on the flight and if there is capacity
     */
    public static List<Reservation> findByUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId required");
        }

        List<Reservation> results = new ArrayList<>();

        try {
            String sql =
                "SELECT reservation_id, customer_id, flight_id, seats, status, booking_time " +
                "FROM reservation WHERE customer_id = ?";

            PreparedStatement stmt = DB.prepare(sql);
            DB.set(stmt, 1, userId.trim());

            ResultSet rs = DB.query(stmt);

            while (DB.next(rs)) {
                String reservationId = DB.getString(rs, "reservation_id");
                String customerId    = DB.getString(rs, "customer_id");
                String flightId      = DB.getString(rs, "flight_id");
                String seatsStr      = DB.getString(rs, "seats");
                String statusStr     = DB.getString(rs, "status");
                String bookingTime   = DB.getString(rs, "booking_time");

                int seats = 0;
                if (seatsStr != null && !seatsStr.isBlank()) {
                    try {
                        seats = Integer.parseInt(seatsStr);
                    } catch (NumberFormatException ignored) {}
                }

                LocalDateTime bookingDateTime = null;
                if (bookingTime != null && !bookingTime.isBlank()) {
                    try {
                        bookingDateTime = LocalDateTime.parse(bookingTime);
                    } catch (Exception ignored) {}
                }

                ReservationStatus status;
                try {
                    // handle enum names like CREATED / CONFIRMED
                    status = ReservationStatus.valueOf(statusStr.toUpperCase());
                } catch (Exception ex) {
                    // if sample data has 'booked', map it to CONFIRMED
                    if ("booked".equalsIgnoreCase(statusStr)) {
                        status = ReservationStatus.CONFIRMED;
                    } else {
                        status = ReservationStatus.CREATED;
                    }
                }

                //Now we have the flight object and calling the function
                Flight flight = FlightCrud.findFlightById(flightId);
                if (flight == null) {
                    // Flight was not there for some reason
                    continue;
                }

                // Build a lightweight User object; customer-specific details
                // can be resolved later if/when needed.
                User user = new User(
                        customerId,
                        "Customer " + customerId,
                        "",          // password unknown at this point
                        "customer"   // role; stored as text in the user table
                );

                // and finally we can create our flight reservation object
                Reservation reservation =
                        new Reservation(
                                reservationId,
                                user,
                                flight,
                                status,
                                seats,
                                bookingDateTime
                        );

                results.add(reservation);
            }

        } catch (RuntimeException e) {
            System.err.println("Error loading reservations for user " + userId + ": " + e.getMessage());
            throw e;
        }

        return results;
    }

    /**
     * Load all reservations for a given flight (flight_id).
     */
    public static List<Reservation> findByFlightId(String flightId) {
        if (flightId == null || flightId.isBlank()) {
            throw new IllegalArgumentException("flightId required");
        }

        List<Reservation> results = new ArrayList<>();

        try {
            String sql =
                "SELECT reservation_id, customer_id, flight_id, seats, status, booking_time " +
                "FROM reservation WHERE flight_id = ?";

            PreparedStatement stmt = DB.prepare(sql);
            DB.set(stmt, 1, flightId.trim());

            ResultSet rs = DB.query(stmt);

            while (DB.next(rs)) {
                String reservationId = DB.getString(rs, "reservation_id");
                String customerId    = DB.getString(rs, "customer_id");
                String dbFlightId    = DB.getString(rs, "flight_id");
                String seatsStr      = DB.getString(rs, "seats");
                String statusStr     = DB.getString(rs, "status");
                String bookingTime   = DB.getString(rs, "booking_time");

                int seats = 0;
                if (seatsStr != null && !seatsStr.isBlank()) {
                    try {
                        seats = Integer.parseInt(seatsStr);
                    } catch (NumberFormatException ignored) {}
                }

                LocalDateTime bookingDateTime = null;
                if (bookingTime != null && !bookingTime.isBlank()) {
                    try {
                        bookingDateTime = LocalDateTime.parse(bookingTime);
                    } catch (Exception ignored) {}
                }

                ReservationStatus status;
                try {
                    status = ReservationStatus.valueOf(statusStr.toUpperCase());
                } catch (Exception ex) {
                    if ("booked".equalsIgnoreCase(statusStr)) {
                        status = ReservationStatus.CONFIRMED;
                    } else {
                        status = ReservationStatus.CREATED;
                    }
                }

                Flight flight = FlightCrud.findFlightById(dbFlightId);
                if (flight == null) {
                    continue;
                }

                User user = new User(
                        customerId,
                        "Customer " + customerId,
                        "",
                        "customer"
                );

                Reservation reservation =
                        new Reservation(
                                reservationId,
                                user,
                                flight,
                                status,
                                seats,
                                bookingDateTime
                        );

                results.add(reservation);
            }

        } catch (RuntimeException e) {
            System.err.println("Error loading reservations for flight " + flightId + ": " + e.getMessage());
            throw e;
        }

        return results;
    }

    //See over dev we realized that we needed a getReservationbyID, so it can get a single object so of the
    //customer reservation object so it can be accessed by PAYMENT, I tried using our list but that didnt make sense either
    // why would we build all reservation objects only to pull one

    //I am going to reuse most of my code in the above function
    //Shoutout to CHATGPT for the help identifying the need for this during our debugging
    //again its very derivative of what we did the ocnstruction of all the previous CRUD, however
    //CHATGPT deserves a ton of credit for it's implementation.

    public static Reservation getReservationById(String reservationId) {
    if (reservationId == null || reservationId.isBlank()) {
        throw new IllegalArgumentException("reservationId required");
    }

    try {
        String sql =
            "SELECT reservation_id, customer_id, flight_id, seats, status, booking_time " +
            "FROM reservation WHERE reservation_id = ?";

        PreparedStatement stmt = DB.prepare(sql);
        DB.set(stmt, 1, reservationId.trim());

        ResultSet rs = DB.query(stmt);

        if (!DB.next(rs)) {
            return null; // no such reservation
        }

        String resId      = DB.getString(rs, "reservation_id");
        String customerId = DB.getString(rs, "customer_id");
        String flightId   = DB.getString(rs, "flight_id");
        String seatsStr   = DB.getString(rs, "seats");
        String statusStr  = DB.getString(rs, "status");
        String bookingStr = DB.getString(rs, "booking_time");

        int seats = 0;
        if (seatsStr != null && !seatsStr.isBlank()) {
            try {
                seats = Integer.parseInt(seatsStr);
            } catch (NumberFormatException ignored) {}
        }

        LocalDateTime bookingDateTime = null;
        if (bookingStr != null && !bookingStr.isBlank()) {
            try {
                bookingDateTime = LocalDateTime.parse(bookingStr);
            } catch (Exception ignored) {}
        }

        ReservationStatus status;
        try {
            status = ReservationStatus.valueOf(statusStr.toUpperCase());
        } catch (Exception ex) {
            if ("booked".equalsIgnoreCase(statusStr)) {
                status = ReservationStatus.CONFIRMED;
            } else {
                status = ReservationStatus.CREATED;
            }
        }

        // Rebuild Flight from DB (truth is in DB)
        Flight flight = FlightCrud.findFlightById(flightId);
        if (flight == null) {
            return null;
        }

        // Build a lightweight User object; customer-specific details
        // can be resolved later if/when needed.
        User user = new User(
                customerId,
                "Customer " + customerId,
                "",          // password unknown at this point
                "customer"   // role; stored as text in the user table
        );

        return new Reservation(
                resId,
                user,
                flight,
                status,
                seats,
                bookingDateTime
        );

    } catch (RuntimeException e) {
        System.err.println("Error loading reservation " + reservationId + ": " + e.getMessage());
        throw e;
    }
    }

    /**
     * Update the status of a reservation.
     */
    public static void updateStatus(String reservationId, ReservationStatus newStatus) {
        if (reservationId == null || reservationId.isBlank()) {
            throw new IllegalArgumentException("reservationId required");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("newStatus required");
        }

        try {
            String sql = "UPDATE reservation SET status = ? WHERE reservation_id = ?";

            PreparedStatement stmt = DB.prepare(sql);
            DB.set(stmt, 1, newStatus.name());
            DB.set(stmt, 2, reservationId.trim());

            DB.update(stmt);
        } catch (RuntimeException e) {
            System.err.println("Error updating reservation status for " + reservationId + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Delete a reservation row by ID.
     */
    public static void deleteById(String reservationId) {
        if (reservationId == null || reservationId.isBlank()) {
            throw new IllegalArgumentException("reservationId required");
        }

        try {
            String sql = "DELETE FROM reservation WHERE reservation_id = ?";

            PreparedStatement stmt = DB.prepare(sql);
            DB.set(stmt, 1, reservationId.trim());

            DB.update(stmt);
        } catch (RuntimeException e) {
            System.err.println("Error deleting reservation " + reservationId + ": " + e.getMessage());
            throw e;
        }
    }
}