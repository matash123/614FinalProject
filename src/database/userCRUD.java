package src.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import src.models.User;

public class userCRUD {

    
    //Look up an active user by username.
     //Returns null if no user is found or an error occurs.
    public static User getUser(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }

        try {
            // Now implementing the the true, 
            String sql =
                "SELECT id, username, password, role " +
                "FROM user " +
                "WHERE username = ? AND active = 1";

            PreparedStatement stmt = DB.prepare(sql);
            DB.set(stmt, 1, username.trim());

            //pulling from the db now
            ResultSet rs = DB.query(stmt);

            if (DB.next(rs)) {
                // User constructor: (userId, name, password, role). This is to match our schema exactlty
                return new User(
                    DB.getString(rs, "id"),
                    DB.getString(rs, "username"),
                    DB.getString(rs, "password"),
                    DB.getString(rs, "role")
                );
            }

            // Quick check than can resturn null
            return null;
        
        //Quick runtime exception, this is a reference to CHATGPT for the idea and the help implementing
        //This step always trips me up in writing the correct syntax
        } catch (RuntimeException e) {
            System.err.println("Error in userCRUD.getUser: " + e.getMessage());
            return null;
        }
    }
}