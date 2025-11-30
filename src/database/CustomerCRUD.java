package src.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import src.models.Customer;

/**
 * Simple CRUD helper for {@link Customer}-specific data that lives in the
 * {@code customer} table and extends the base {@code user} row.
 *
 * The base {@link src.models.User} fields (name, email, password, role)
 * remain managed by {@link userCRUD}; this class focuses on the extra
 * customer profile details such as rewards number and contact info.
 */
public class CustomerCRUD {

    /**
     * Load a full {@link Customer} aggregate (user + customer extras) for
     * the given user id. Returns {@code null} if the user does not exist
     * or is not a customer.
     */
    public static Customer getCustomerById(String userId) {
        if (userId == null || userId.isBlank()) {
            return null;
        }

        try {
            String sql =
                "SELECT u.id, u.name, u.email, u.password, u.role, " +
                "       c.rewards_number, c.address, c.phone, c.credit_card " +
                "FROM user u " +
                "LEFT JOIN customer c ON c.id = u.id " +
                "WHERE u.id = ?";

            PreparedStatement stmt = DB.prepare(sql);
            DB.set(stmt, 1, userId.trim());
            ResultSet rs = DB.query(stmt);

            if (!DB.next(rs)) {
                return null;
            }

            String id = DB.getString(rs, "id");
            String name = DB.getString(rs, "name");
            String email = DB.getString(rs, "email");
            String password = DB.getString(rs, "password");

            String rewards = nullSafe(DB.getString(rs, "rewards_number"));
            String address = nullSafe(DB.getString(rs, "address"));
            String phone   = nullSafe(DB.getString(rs, "phone"));
            String credit  = nullSafe(DB.getString(rs, "credit_card"));

            Customer customer = new Customer(
                id,
                name,
                password,
                rewards,
                address,
                phone
            );
            customer.setEmail(email);
            customer.setCreditCardNumber(credit);

            return customer;

        } catch (RuntimeException e) {
            System.err.println("Error in CustomerCRUD.getCustomerById: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Update or insert the customer-specific profile data.
     *
     * The base {@code user} fields (name, email, password) are not touched here.
     */
    public static void upsertCustomerProfile(
        String userId,
        String rewardsNumber,
        String address,
        String phone,
        String creditCard
    ) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }

        try {
            String sql =
                "UPDATE customer " +
                "SET rewards_number = ?, address = ?, phone = ?, credit_card = ? " +
                "WHERE id = ?";

            PreparedStatement stmt = DB.prepare(sql);
            DB.set(stmt, 1, emptyToNull(rewardsNumber));
            DB.set(stmt, 2, emptyToNull(address));
            DB.set(stmt, 3, emptyToNull(phone));
            DB.set(stmt, 4, emptyToNull(creditCard));
            DB.set(stmt, 5, userId.trim());

            int updated = DB.update(stmt);

            if (updated == 0) {
                // No existing row; insert a new one for this customer id.
                String insertSql =
                    "INSERT INTO customer (id, rewards_number, address, phone, credit_card) " +
                    "VALUES (?, ?, ?, ?, ?)";
                PreparedStatement insert = DB.prepare(insertSql);
                DB.set(insert, 1, userId.trim());
                DB.set(insert, 2, emptyToNull(rewardsNumber));
                DB.set(insert, 3, emptyToNull(address));
                DB.set(insert, 4, emptyToNull(phone));
                DB.set(insert, 5, emptyToNull(creditCard));
                DB.update(insert);
            }

        } catch (RuntimeException e) {
            System.err.println("Error in CustomerCRUD.upsertCustomerProfile: " + e.getMessage());
            throw e;
        }
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s;
    }

    private static String emptyToNull(String s) {
        if (s == null) return null;
        String trimmed = s.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}


