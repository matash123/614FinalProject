package domain;

import java.time.LocalDateTime;

/**
 * Represents a booking of a flight by a customer.
 * Maps to the reservation table.
 */
public class FlightCustomerReservation {

    private final String reservationId;   // this what stores the reservation and connects customer & reservation & demolishes a many to many class linkage
    private Customer customer; // holding the customer so we can again access the customerID as a secondary key
    private Flight flight; // we want the flightID as our secondary key
    private bool payedStatus;
    private int seats;
    private LocalDateTime bookingDateTime;

    public FlightCustomerReservation(String reservationId,
                       Customer customer,
                       Flight flight,
                       ReservationStatus status,
                       int seats,
                       LocalDateTime bookingDateTime) {

        this.reservationId = reservationId;
        this.customer = customer;
        this.flight = flight;
        this.status = status;
        this.seats = seats;
        this.bookingDateTime = bookingDateTime;
    }

    public String getReservationId() {
        return reservationId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Flight getFlight() {
        return flight;
    }

    public bool getStatus() {
        return status;
    }

    public int getSeats() {
        return seats;
    }

    public LocalDateTime getBookingDateTime() {
        return bookingDateTime;
    }

    //Specic Methods to this fiedl

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
