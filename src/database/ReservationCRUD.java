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
     * Load all reservations for a given flight.
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

                Flight flight = FlightCrud.findFlightById(flightId);
                if (flight == null) {
                    continue;
                }

                User user = new User(
                        customerId,
                        "Customer " + customerId,
                        "",          // password unknown at this point
                        "customer"   // role; stored as text in the user table
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

    /**
     * Delete a reservation by id.
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