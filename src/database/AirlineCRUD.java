/**
 * 
 * This is where things are starting to come together under control and we are seeing how to properly use our
 * inteded structure, this is going to be our CRUD of airlines
 * 
 * By design this accessed by FLIGHTAGENT who can use existing AIRLINES and AIRPLANES to CREATE NEW FLIGHTS!!
 * but thats where the functionality ends
 * 
 * AS FLIGHTS are dependent on both AIRPLANES & AIRLINES, this where SYSTEM ADMINSTRATOR comes into place, there functionality
 * extends FLIGHT AGENT as they are only ones who can create AIRPLANES and AIRLINES
 * 
 * I think we are super proud of this execution as this shows extended control in a meaningful way that makes sense in a true system
 * an agent should not have that much control (in the real world they are client facing entity), but shouldnt really have any ability with
 * add new airline accounts and the airplanes that exist within them (as we of course have AIRPLANE live under an AIRLINE)
 */

package src.database;

//needed imports, we built FLIGHT CRUD first so this very much in the same tune and whole dev processed followed that structure
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import src.models.Airline;

public class AirlineCrud {


    //This is very much to be executed again by SYSTEM ADMINISTRATOR

    public static void saveAirline(Airline airline) {
        if (airline == null) {
            throw new IllegalArgumentException("airline cannot be null");
        }

        String sql =
            "INSERT INTO airline (airline_id, name) " +
            "VALUES (?, ?) " +
            "ON CONFLICT(airline_id) DO UPDATE SET " +
            "  name = excluded.name";

        PreparedStatement stmt = DB.prepare(sql);
        DB.set(stmt, 1, airline.getAirlineId());
        DB.set(stmt, 2, airline.getName());

        DB.update(stmt);
    }

    //This is deleting an airline, which takes the AIRLINE ID and as such deletes accordingly
    public static void deleteAirline(String airlineId) {
        if (airlineId == null || airlineId.isBlank()) {
            throw new IllegalArgumentException("airlineId required");
        }

        String sql = "DELETE FROM airline WHERE airline_id = ?";

        PreparedStatement stmt = DB.prepare(sql);
        DB.set(stmt, 1, airlineId);

        DB.update(stmt);
    }

    //NOW OUR SEARCH query for AIRLINES, this is intended to be used by both SYS ADMIN, but also our FLIGHT AGENT, when they add a flight
    //they have to be able to choose an airline, 

    //TO DO : Potential
    //COOL THOUGHT for furture enhancement WHICH WE MAY NOT HAVE TIME TO IMPLEMENT, but to do a check if the AIRLINE has an available plane for the day of choice
    //when the flight agent creates the flight and then the same in OUR AIRPLANE crud, but lets see if we get to it
    public static List<Airline> findAll() {
        List<Airline> airlines = new ArrayList<>();

        String sql = "SELECT airline_id, name FROM airline";

        PreparedStatement stmt = DB.prepare(sql);
        ResultSet rs = DB.query(stmt);

        while (DB.next(rs)) {
            String id   = DB.getString(rs, "airline_id");
            String name = DB.getString(rs, "name");
            airlines.add(new Airline(id, name));
        }

        return airlines;
    }

    //last basic search, I think we will need which is the ability to search for a specific airline by it' ID
    // I think is probably needed by System Administrator if they want see if they already have an account from the Airline,
    // and if not to register or pursue registration, but really for FLIGHT AGENT so that if they want to book a flight they can find the airline
    // BUT WE HAVENT EXECUTED THIS CONTROLLER LOGIC yet, so may not get to it
    //But cause I was on roll thought may as well build this functionality
    public static Airline findById(String airlineId) {
        if (airlineId == null || airlineId.isBlank()) {
            throw new IllegalArgumentException("airlineId required");
        }

        String sql = "SELECT airline_id, name FROM airline WHERE airline_id = ?";

        PreparedStatement stmt = DB.prepare(sql);
        DB.set(stmt, 1, airlineId);

        ResultSet rs = DB.query(stmt);
        if (!DB.next(rs)) {
            return null;
        }

        String id   = DB.getString(rs, "airline_id");
        String name = DB.getString(rs, "name");
        return new Airline(id, name);
    }
}

