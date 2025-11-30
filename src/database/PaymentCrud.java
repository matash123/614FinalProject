package src.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import src.models.Payment;
import src.models.PaymentStatus;
import src.models.Reservation;

/**
 * CRUD operations for the {@link Payment} entity.
 * Persists to and loads from the {@code payment} table.
 */
public class PaymentCrud {

    /**
     * Insert a new payment row.
     */
    public static void savePayment(Payment p) {
        if (p == null) {
            throw new IllegalArgumentException("payment cannot be null");
        }
        Reservation r = p.getReservation();
        if (r == null) {
            throw new IllegalArgumentException("payment must be linked to a reservation");
        }

        try {
            String sql =
                "INSERT INTO payment (" +
                "  payment_id, reservation_id, amount, status, timestamp" +
                ") VALUES (?, ?, ?, ?, ?)";

            PreparedStatement stmt = DB.prepare(sql);
            DB.set(stmt, 1, p.getPaymentId());
            DB.set(stmt, 2, r.getReservationId());
            DB.set(stmt, 3, Double.toString(p.getAmount()));
            DB.set(stmt, 4, p.getStatus().name());

            LocalDateTime ts = p.getTimestamp();
            DB.set(stmt, 5, ts != null ? ts.toString() : null);

            DB.update(stmt);
        } catch (RuntimeException e) {
            System.err.println("Error saving payment: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Load all payments for a given reservation.
     * Note: this returns Payment objects without fully-hydrated Reservation;
     * callers can resolve the reservation separately if needed.
     */
    public static List<Payment> findByReservationId(String reservationId) {
        if (reservationId == null || reservationId.isBlank()) {
            throw new IllegalArgumentException("reservationId required");
        }

        List<Payment> results = new ArrayList<>();

        try {
            String sql =
                "SELECT payment_id, amount, status, timestamp " +
                "FROM payment WHERE reservation_id = ?";

            PreparedStatement stmt = DB.prepare(sql);
            DB.set(stmt, 1, reservationId.trim());

            ResultSet rs = DB.query(stmt);

            while (DB.next(rs)) {
                String paymentId = DB.getString(rs, "payment_id");
                String amountStr = DB.getString(rs, "amount");
                String statusStr = DB.getString(rs, "status");
                String tsStr     = DB.getString(rs, "timestamp");

                double amount = 0.0;
                if (amountStr != null && !amountStr.isBlank()) {
                    try {
                        amount = Double.parseDouble(amountStr);
                    } catch (NumberFormatException ignored) {}
                }

                PaymentStatus status;
                try {
                    status = PaymentStatus.valueOf(statusStr.toUpperCase());
                } catch (Exception ex) {
                    status = PaymentStatus.FAILED;
                }

                LocalDateTime ts = null;
                if (tsStr != null && !tsStr.isBlank()) {
                    try {
                        ts = LocalDateTime.parse(tsStr);
                    } catch (Exception ignored) {}
                }

                // We don't hydrate Reservation here; pass null and let callers
                // associate the payment with a reservation if needed.
                Payment p = new Payment(paymentId, null, amount, status, ts);
                results.add(p);
            }

        } catch (RuntimeException e) {
            System.err.println("Error loading payments for reservation " + reservationId + ": " + e.getMessage());
            throw e;
        }

        return results;
    }
}


