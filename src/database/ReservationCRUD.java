package src.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import src.models.Customer;
import src.models.Flight;
import src.models.FlightCustomerReservation;
import src.models.ReservationStatus;

public class ReservationCRUD {

    /**
     * Insert a new reservation row.
     * We only store the link to customer + flight (via IDs).
     */
    public static void saveReservation(FlightCustomerReservation r) {
        if (r == null) {
            throw new IllegalArgumentException("reservation cannot be null");
        }
        if (r.getCustomer() == null || r.getFlight() == null) {
            throw new IllegalArgumentException("customer and flight are required on reservation");
        }

        try {
            String sql =
                "INSERT INTO reservation (" +
                "  reservation_id, customer_id, flight_id, seats, status, booking_time" +
                ") VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = DB.prepare(sql);
            DB.set(stmt, 1, r.getReservationId());
            DB.set(stmt, 2, r.getCustomer().getUserId());
            DB.set(stmt, 3, r.getFlight().getFlightId());
            DB.set(stmt, 4, r.getSeats());
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
    public static List<FlightCustomerReservation> findByUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId required");
        }

        List<FlightCustomerReservation> results = new ArrayList<>();

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

                // Now we need to build the customer object from the database
                Customer customer = new Customer(
                        customerId,
                        "Customer " + customerId,
                        "" // password unknown at this point
                );

                //and finally we can create our flight reservation object
                FlightCustomerReservation reservation =
                        new FlightCustomerReservation(
                                reservationId,
                                customer,
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

    //See over dev we realized that we needed a getReservationbyID, so it can get a single object so of the
    //customer reservation object so it can be accessed by PAYMENT, I tried using our list but that didnt make sense either
    // why would we build all reservation objects only to pull one

    //I am going to reuse most of my code in the above function
    //Shoutout to CHATGPT for the help identifying the need for this during our debugging
    //again its very derivative of what we did the ocnstruction of all the previous CRUD, however
    //CHATGPT deserves a ton of credit for it's implementation.

    public static FlightCustomerReservation getReservationById(String reservationId) {
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

        // Minimal Customer object (you can refine later)
        Customer customer = new Customer(
                customerId,
                "Customer " + customerId,
                "" // password unknown here
        );

        return new FlightCustomerReservation(
                resId,
                customer,
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

}