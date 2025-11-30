/**
 * Okay sweet this was the last CRUD we built after Flight and Airline, so by now this was quite easy to implement
 * Resued a lot of same code and mofied for this specific functionality
 * ChatGPt reference as it was used to help set up FLIGHT and debug and then we have reused a lot of the same logic
 * just modified ourselves and thought functionality, so was super helpful and we have a great understanding of setting up
 * crud and speaking to database by the time we got here.
 * 
 * Airplane again is owned by an Airline and used in FLIGHT BOOKING
 * SYS ADMIN is the one who can actually create an AIRPLANE (which is intended and we believe makes most sense conceptually)
 * And the Flight Agent can search airplanes (needed and dependent to create ) the actuall flight object
 * this was intended so they can see if they even have a plane available on the date to create a flight, HOWEVER we may not have time to fully flush this out
 * but that was our original intention and we believe makes the most sense to represent a TRUE SYSTEM.
*/

package src.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import src.models.Airplane;

public class AirplaneCRUD {

    // SYS ADMIN ability create or update airplane, tied to an airline
    public static void addAirplane(Airplane airplane, String airlineId) {
        if (airplane == null) {
            throw new IllegalArgumentException("airplane cannot be null");
        }
        if (airlineId == null || airlineId.isBlank()) {
            throw new IllegalArgumentException("airlineId is required for airplane");
        }

        String sql =
            "INSERT INTO airplane (airplane_id, airline_id, model, manufacturer, capacity) " +
            "VALUES (?, ?, ?, ?, ?) " +
            "ON CONFLICT(airplane_id) DO UPDATE SET " +
            "  airline_id   = excluded.airline_id, " +
            "  model        = excluded.model, " +
            "  manufacturer = excluded.manufacturer, " +
            "  capacity     = excluded.capacity";

        PreparedStatement stmt = DB.prepare(sql);
        DB.set(stmt, 1, airplane.getAirplaneId());
        DB.set(stmt, 2, airlineId);
        DB.set(stmt, 3, airplane.getModel());
        DB.set(stmt, 4, airplane.getManufacturer());
        DB.set(stmt, 5, Integer.toString(airplane.getCapacity()));

        DB.update(stmt);
    }

    // SYS ADMIN function to be able to delete an airplane, again same as before tied to an airline
    public static void deleteAirplane(String airplaneId) {
        if (airplaneId == null || airplaneId.isBlank()) {
            throw new IllegalArgumentException("airplaneId required");
        }

        String sql = "DELETE FROM airplane WHERE airplane_id = ?";

        PreparedStatement stmt = DB.prepare(sql);
        DB.set(stmt, 1, airplaneId);

        DB.update(stmt);
    }


    // This is really inteded for FLIGHT AGENT And returns ist airplanes (flight agent will use this for when creating a flight)
    // VERY SIMILAR TO WHAT WE IMPLEMENTED in airline, just modified so by now we were rolling and did this fast with confidence
    //really happy with how its coming together at this point
    public static List<Airplane> findAll() {
        List<Airplane> planes = new ArrayList<>();

        String sql =
            "SELECT airplane_id, model, manufacturer, capacity " +
            "FROM airplane";

        PreparedStatement stmt = DB.prepare(sql);
        ResultSet rs = DB.query(stmt);

        while (DB.next(rs)) {
            String id     = DB.getString(rs, "airplane_id");
            String model  = DB.getString(rs, "model");
            String mfg    = DB.getString(rs, "manufacturer");
            String capStr = DB.getString(rs, "capacity");

            int capacity = 0;
            if (capStr != null && !capStr.isBlank()) {
                try {
                    capacity = Integer.parseInt(capStr);
                } catch (NumberFormatException ignored) {
                    capacity = 150;
                }
            }

            planes.add(new Airplane(id, model, mfg, capacity));
        }

        return planes;
    }

    // Again same idea as in AIRLINECRUD (where comments live, ability to search for a specific plane), intended to be predominantly used by FLIGHT AGENt when creating a flight
    public static Airplane findById(String airplaneId) {
        if (airplaneId == null || airplaneId.isBlank()) {
            throw new IllegalArgumentException("airplaneId required");
        }

        String sql =
            "SELECT airplane_id, model, manufacturer, capacity " +
            "FROM airplane WHERE airplane_id = ?";

        PreparedStatement stmt = DB.prepare(sql);
        DB.set(stmt, 1, airplaneId);

        ResultSet rs = DB.query(stmt);
        if (!DB.next(rs)) {
            return null;
        }

        String id     = DB.getString(rs, "airplane_id");
        String model  = DB.getString(rs, "model");
        String mfg    = DB.getString(rs, "manufacturer");
        String capStr = DB.getString(rs, "capacity");

        int capacity = 0;
        if (capStr != null && !capStr.isBlank()) {
            try {
                capacity = Integer.parseInt(capStr);
            } catch (NumberFormatException ignored) {
                capacity = 150;
            }
        }

        return new Airplane(id, model, mfg, capacity);
    }

    //next step to update repository bridge to reflect all this
}
