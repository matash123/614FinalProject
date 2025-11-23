package src.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import src.config.envLoader;

public class DB {
    private static Connection conn; //singleton conncetion

    public static Connection getConnection() throws RuntimeException {
        if (conn == null) {

            String dbPath = envLoader.get("DB_PATH"); //uses db from environment to allow us to change db

            if (dbPath == null || dbPath.isEmpty()) {
                throw new RuntimeException("DB_PATH environment variable not set");
            }

            String url = "jdbc:sqlite:" + dbPath; //append java connection syntax

            try{
                conn = DriverManager.getConnection(url);
            } catch(SQLException e) { //catch driverManager connection error and turn into a runtime exception.
                throw new RuntimeException("Failed to connect to DB: " + e.getMessage(), e); //this allows us to getConnection in other files without doing try catch blocks. 
            }
        }
        return conn;
    }

    // Prepare statement helper
    public static PreparedStatement prepare(String sql) {
        try {
            return getConnection().prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Prepare failed: " + e.getMessage(), e);
        }
    }

    // Execute a query
    public static ResultSet query(PreparedStatement stmt) {
        try {
            return stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Query failed: " + e.getMessage(), e);
        }
    }

    // Execute insert/update/delete
    public static int update(PreparedStatement stmt) {
        try {
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Update failed: " + e.getMessage(), e);
        }
    }

    public static void set(PreparedStatement stmt, int pos, String val){
        try {
            stmt.setString(pos, val);
        } catch (SQLException e) {
            throw new RuntimeException("SQL Statement is invalid" + e.getMessage(), e);
        }
    }

    public static boolean next(ResultSet rs){
        try{
            return rs.next();
        } catch (SQLException e){
            throw new RuntimeException("No results found in database" + e.getMessage(), e);
        }
    }

    public static String getString(ResultSet rs, String key){
        try{
            return rs.getString(key);
        } catch (SQLException e){
            throw new RuntimeException("Key not found" + e.getMessage(), e);
        }
    }
}
