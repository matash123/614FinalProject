package src.models;

import java.time.LocalDateTime;

/**
 * Represents a booking of a flight by a user with the CUSTOMER role.
 * Maps to the reservation table.
 *
 * Note: we intentionally depend on {@link User} here rather than the
 * {@link Customer} subtype so that booking logic only needs to know
 * about the authenticated user and their role. Customer-specific data
 * can be fetched separately when needed.
 */
public class Reservation {

    // connects user & flight and captures reservation-specific data
    private final String reservationId;
    private User user;
    private Flight flight;
    private ReservationStatus status;   // life-cycle status of the reservation
    private boolean payedStatus;
    private int seats;
    private LocalDateTime bookingDateTime;

    public Reservation(String reservationId,
                       User user,
                       Flight flight,
                       ReservationStatus status,
                       int seats,
                       LocalDateTime bookingDateTime) {

        this.reservationId = reservationId;
        this.user = user;
        this.flight = flight;
        this.status = status;
        this.seats = seats;
        this.bookingDateTime = bookingDateTime;
    }

    public String getReservationId() {
        return reservationId;
    }

    public User getUser() {
        return user;
    }

    public Flight getFlight() {
        return flight;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public int getSeats() {
        return seats;
    }

    public LocalDateTime getBookingDateTime() {
        return bookingDateTime;
    }

    // Specific methods to this field

    public void confirm() {
        if (payedStatus == false) {
            throw new IllegalStateException("Cannot confirm a cancelled reservation");
        }
        payedStatus = true;
    }

    public void cancel() {
        if (payedStatus == false) {
            return; // already cancelled
        }
        payedStatus = false;
        // releasing seats will be handled by the controller/service
    }

    public boolean isActive() {
        return status == ReservationStatus.CREATED
                || status == ReservationStatus.PENDING_PAYMENT
                || status == ReservationStatus.CONFIRMED;
    }
}


