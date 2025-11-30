package src.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import src.models.User;

public class userCRUD {

    // Look up an active user by username.
    // Returns null if no user is found or an error occurs.
    public static User getUser(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }

        try {
            String sql =
                "SELECT id, username, password, role " +
                "FROM user " +
                "WHERE username = ? AND active = 1";

            PreparedStatement stmt = DB.prepare(sql);
            DB.set(stmt, 1, username.trim());

            ResultSet rs = DB.query(stmt);

            if (DB.next(rs)) {
                // User constructor: (userId, name, password, role).
                return new User(
                    DB.getString(rs, "id"),
                    DB.getString(rs, "username"),
                    DB.getString(rs, "password"),
                    DB.getString(rs, "role")
                );
            }

            return null;

        } catch (RuntimeException e) {
            System.err.println("Error in userCRUD.getUser: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Load all active users from the database.
     */
    public static List<User> findAllActive() {
        List<User> results = new ArrayList<>();

        try {
            String sql =
                "SELECT id, username, password, role " +
                "FROM user " +
                "WHERE active = 1";

            PreparedStatement stmt = DB.prepare(sql);
            ResultSet rs = DB.query(stmt);

            while (DB.next(rs)) {
                User u = new User(
                    DB.getString(rs, "id"),
                    DB.getString(rs, "username"),
                    DB.getString(rs, "password"),
                    DB.getString(rs, "role")
                );
                results.add(u);
            }

        } catch (RuntimeException e) {
            System.err.println("Error in userCRUD.findAllActive: " + e.getMessage());
            throw e;
        }

        return results;
    }

    /**
     * Create a new active customer user with the given username and password.
     *
     * For now we treat "name" and "username" as the same display value and
     * do not require an email address.
     */
    public static User createCustomer(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username is required");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password is required");
        }

        try {
            // Ensure the username is unique.
            String checkSql = "SELECT 1 FROM user WHERE username = ?";
            PreparedStatement checkStmt = DB.prepare(checkSql);
            DB.set(checkStmt, 1, username.trim());
            ResultSet rs = DB.query(checkStmt);
            if (DB.next(rs)) {
                throw new IllegalArgumentException("That username is already taken.");
            }

            String id = "CUST_" + UUID.randomUUID();

            String insertSql =
                "INSERT INTO user (id, name, username, email, password, active, role) " +
                "VALUES (?, ?, ?, ?, ?, 1, 'customer')";

            PreparedStatement insertStmt = DB.prepare(insertSql);
            DB.set(insertStmt, 1, id);
            // For now, use the username for both name and username columns.
            DB.set(insertStmt, 2, username.trim());
            DB.set(insertStmt, 3, username.trim());
            DB.set(insertStmt, 4, null);          // email optional / unused for now
            DB.set(insertStmt, 5, password);      // plain-text for now, matches sample data

            DB.update(insertStmt);

            // Also insert into the customer subtype table so relationships remain consistent.
            PreparedStatement subtypeStmt = DB.prepare("INSERT INTO customer (id) VALUES (?)");
            DB.set(subtypeStmt, 1, id);
            DB.update(subtypeStmt);

            return new User(id, username.trim(), password, "customer");

        } catch (RuntimeException e) {
            System.err.println("Error in userCRUD.createCustomer: " + e.getMessage());
            throw e;
        }
    }
}