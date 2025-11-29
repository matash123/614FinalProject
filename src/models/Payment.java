package src.models;

import java.time.LocalDateTime;

/**
 * Represents a payment attempt for a reservation.
 * Maps to the payment table.
 */
public class Payment {

    private final String paymentId;   // PK
    private FlightCustomerReservation reservation;
    private double amount;
    private PaymentStatus status;
    private LocalDateTime timestamp;

    public Payment(String paymentId,
                   FlightCustomerReservation reservation,
                   double amount,
                   PaymentStatus status,
                   LocalDateTime timestamp) {

        this.paymentId = paymentId;
        this.reservation = reservation;
        this.amount = amount;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public FlightCustomerReservation getReservation() {
        return reservation;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Extra functionality I believe will be relevant to Payment, based on an ENUM I may create but that is subject to change

    public void markPaid() {
        status = PaymentStatus.PAID;
        timestamp = LocalDateTime.now();
    }

    public void markFailed() {
        status = PaymentStatus.FAILED;
        timestamp = LocalDateTime.now();
    }

    public boolean isPaid() {
        return status == PaymentStatus.PAID;
    }
}
