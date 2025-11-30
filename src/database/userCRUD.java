package src.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
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
}