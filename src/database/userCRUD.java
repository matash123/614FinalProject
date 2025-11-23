package src.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

import src.entities.User;



public  class userCRUD {
    public static User getUser(String username){
        //all DB helper functions are wrapped to catch errors and throw  runtime exception. 
        //Can wrap entire function into try catch blocks for a runtime error on any DB helper function.    
        
        try{
            //create sql string
            String sql = "SELECT id, userName, password, role FROM user WHERE username = ?";
            
            //c
            PreparedStatement stmt = DB.prepare(sql);

            DB.set(stmt, 1, username);
            ResultSet rs = DB.query(stmt);

            if (DB.next(rs)) {
                return new User(
                    DB.getString(rs, "id"),
                    DB.getString(rs,"username"),
                    DB.getString(rs, "password"),
                    DB.getString(rs,"role")
                );
            } 

            return null;
        } catch(RuntimeException e){System.err.println(e.getMessage());}
        return null;
    }
};

