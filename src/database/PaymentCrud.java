package src.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import src.models.Payment;
import src.models.PaymentStatus;
import src.models.Reservation;

public class PaymentCrud {

    // Reference to CHATGPT for this condiseration on a private Satic function which amp one row to a payment object
    // now its makes stromg sense conceptually.
    private static Payment mapRow(ResultSet rs) throws SQLException {
        String paymentId     = DB.getString(rs, "payment_id");
        String reservationId = DB.getString(rs, "reservation_id");
        double amount        = rs.getDouble("amount");
        String statusStr     = DB.getString(rs, "status");
        String ts            = DB.getString(rs, "timestamp");


        PaymentStatus status = PaymentStatus.valueOf(statusStr.toUpperCase());
        LocalDateTime timestamp = (ts != null) ? LocalDateTime.parse(ts) : null;

        // Now to connect payment to reservation (the specific object), we are loading
        // the Reservation model from the DB for this reservation_id.
        Reservation reservation = ReservationCRUD.getReservationById(reservationId);

        return new Payment(paymentId, reservation, amount, status, timestamp);
    }

    // Method to get payment based on a specifc ID, may be important to check if someone hase apyed
    public static Payment getPaymentById(String paymentId) {
        try {
            String sql = "SELECT * FROM payment WHERE payment_id = ?";

            PreparedStatement stmt = DB.prepare(sql);
            DB.set(stmt, 1, paymentId);

            ResultSet rs = DB.query(stmt);
            if (DB.next(rs)) {
                return mapRow(rs);
            }
            return null;

        } catch (RuntimeException | SQLException e) {
            System.err.println("Error in PaymentCRUD.getPaymentById: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Next a query to get the payment from a specific reservationID number
    public static List<Payment> getPaymentsForReservation(String reservationId) {
        List<Payment> results = new ArrayList<>();
        try {
            String sql = "SELECT * FROM payment WHERE reservation_id = ?";

            PreparedStatement stmt = DB.prepare(sql);
            DB.set(stmt, 1, reservationId);

            ResultSet rs = DB.query(stmt);
            while (DB.next(rs)) {
                results.add(mapRow(rs));
            }
        } catch (RuntimeException | SQLException e) {
            System.err.println("Error in PaymentCRUD.getPaymentsForReservation: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }

    // 3) Insert a new payment row
    public static void insertPayment(Payment payment) {
        try {
            String sql = "INSERT INTO payment (payment_id, reservation_id, amount, status, timestamp) " +
                         "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement stmt = DB.prepare(sql);
            stmt.setString(1, payment.getPaymentId());
            stmt.setString(2, payment.getReservation().getReservationId());
            stmt.setDouble(3, payment.getAmount());
            stmt.setString(4, payment.getStatus().name().toLowerCase());

            LocalDateTime ts = payment.getTimestamp();
            stmt.setString(5, ts != null ? ts.toString() : null);
            System.out.println("payment stmt: " + stmt);
            DB.update(stmt);

        } catch (RuntimeException | SQLException e) {
            System.err.println("Error in PaymentCRUD.insertPayment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //  Now finally updating the payment status such that in can be accessed
    //  which will be critical down the road
    public static void updatePaymentStatus(String paymentId, PaymentStatus newStatus) {
        try {
            String sql = "UPDATE payment SET status = ? WHERE payment_id = ?";

            PreparedStatement stmt = DB.prepare(sql);
            stmt.setString(1, newStatus.name().toLowerCase());
            stmt.setString(2, paymentId);

            DB.update(stmt);

        } catch (RuntimeException | SQLException e) {
            System.err.println("Error in PaymentCRUD.updatePaymentStatus: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
