package src.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List; 
import src.models.Airline;
import src.models.Airplane;
import src.models.Flight;

/**
 * CRUD helpers for the Flight entity.
 * This class is responsible for talking to the DB and constructing
 * rich model objects. Controllers should not use DB directly.
 */
public class FlightCrud {

    /**
     * Basic search by origin/destination and optional exact departure date.
     * Adjust the SQL/table/column names here to match your actual schema.
     */
    public static List<Flight> searchFlights(String origin, String destination, String date, String search_id) {
        List<Flight> flights = new ArrayList<>();

        try {

            //CREATE SQL QUERY (now fixed with correct matching our SCHEMA so this way we are reaching our DATABASe), and connected to Airline
            // I was missing airplane table, which now created and joined in our search - my bad
        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "  flight.flight_id      AS id, " +
            "  flight.origin         AS origin, " +
            "  flight.destination    AS destination, " +
            "  flight.date           AS date, " +
            "  airline.name          AS airline, " +
            "  airline.airline_id    AS airline_id, " +
            "  airplane.airplane_id  AS airplane_id, " +
            "  airplane.model        AS airplane_model, " +
            "  airplane.manufacturer AS airplane_manufacturer, " +
            "  airplane.capacity     AS airplane_capacity, " +
            "  flight.price          AS price " +
            "FROM flight " +
            "JOIN airline  ON flight.airline_id  = airline.airline_id " +
            "JOIN airplane ON flight.airplane_id = airplane.airplane_id " +
            "WHERE 1=1"
        );
            System.out.println("inputs: " + origin + " " + destination + " " + date);

            List<String> params = new ArrayList<>();

            //ERROR HANDLING ON INPUT

            if (origin != null && !origin.isBlank()) {
                sql.append(" AND UPPER(origin) = ?");
                params.add(origin.trim().toUpperCase());
            }
            if (destination != null && !destination.isBlank()) {
                sql.append(" AND UPPER(destination) = ?");
                params.add(destination.trim().toUpperCase());
            }
            if (date != null && !date.isBlank()) {
                // Allow partial date matching using the masked pattern coming
                // from the UI date field (e.g., "2025-__-__" for any day in
                // 2025, "2025-12-__" for any day in December 2025). We rely
                // on SQLite's LIKE semantics where '_' is a single-character
                // wildcard.
                sql.append(" AND date LIKE ?");
                params.add(date.trim());
            }
            if (search_id != null && !search_id.isBlank()) {
                sql.append(" AND flight_id = ?");
                params.add(search_id.trim());
            }

            PreparedStatement stmt = DB.prepare(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                DB.set(stmt, i + 1, params.get(i));
            }
            System.out.println("QUERY: " + sql.toString());
            // SUBMIT QUERY AND STORE RESULTS
            ResultSet rs = DB.query(stmt);

            //LOOP THROUGH RESULTS TO STORE FLIGHTS
            while (DB.next(rs)) {
                String id = DB.getString(rs, "id");
                String dbOrigin = DB.getString(rs, "origin");
                String dbDestination = DB.getString(rs, "destination");
                String dbDate = DB.getString(rs, "date");
                String airlineName = DB.getString(rs, "airline");
                String airlineId = DB.getString(rs, "airline_id");

                String airplaneId = DB.getString(rs, "airplane_id");
                String airplaneModel = DB.getString(rs, "airplane_model");
                String airplaneMfg = DB.getString(rs, "airplane_manufacturer");
                String airplaneCapacityString = DB.getString(rs, "airplane_capacity"); //changed this to string so we can stor ethe actuall parsed version as the true name airplane_Capacity

                String priceStr = DB.getString(rs, "price");

                double price = 0.0;

                //ERROR HANDLING ON PRICE TO NUMBER
                if (priceStr != null && !priceStr.isBlank()) {
                    try {
                        price = Double.parseDouble(priceStr);
                    } catch (NumberFormatException ignored) {}
                }

                //ERROR HANDLING ON DATE -> ASSUMING WE CANT STORE A FLIGHT 
                LocalDate departureDate = null;
                if (dbDate != null && !dbDate.isBlank()) {
                    // expected format: yyyy-MM-dd (matches the UI mask)
                    departureDate = LocalDate.parse(dbDate);
                }

                //handling the fact that Airplane_capacity is the STRING (this was a bad choice in the schema, but easier to deal with here through parsing)
                // reference to CHATGPT for helping me figure this out stuck here for so long! -> great catch and now I know to be very sure with TYPEs
                int airplaneCapacity = 0;
                if (airplaneCapacityString != null && !airplaneCapacityString.isBlank()) {
                    try {
                        airplaneCapacity = Integer.parseInt(airplaneCapacityString);
                    } catch (NumberFormatException ignored) {
                        airplaneCapacity = 150;
                    }
                }


                /** 
                 * NOW WE HAVE AIRPLANE, dont believe we need this anymore
                 * 
                 * 
                // TODO REPLACE AND GET ACTUAL AIRLINE, we cant create a flight without creating an airline model. So we must search and create and airline. 
                // minimal Airline / Airplane construction so the Flight model is complete
                String airlineId = (airlineName != null && airlineName.length() >= 3)
                        ? airlineName.substring(0, 3).toUpperCase()
                        : "GEN";

                */

                 


                Airline airline = new Airline(airlineId, airlineName);
                Airplane airplane = new Airplane (airplaneId, airplaneModel, airplaneMfg, airplaneCapacity);

                Flight f = new Flight(
                    id,
                    airline,
                    dbOrigin,
                    dbDestination,
                    departureDate,
                    airplane,
                    price
                );

                flights.add(f);
            }
        } catch (RuntimeException e) {
            System.err.println("Flight search failed: " + e.getMessage());
        }

        return flights;
    }


    /**OKAY SWEET NOW WE BACK HERE after our original creation of this CRUD this was our first CRUD file we created
     * and we learned alot about what we were missing, and now that both Airline and Airplane crud are created who FLIGHT is dependent on
     * we can come back and make the addFlight
     * THIS IS THE KEY LOGIC WHICH IS EXECUTED by Flight Agent which entails actually creating a new flight which comes
     * from having to pull from an existing AIRLINE & AIRPLANE, which are created and published to DB FROM SYS ADMIN
     * so we are loving the logic and the seperation and containment of roles, which occurs in the actual controller pulling from these CRUD functions,
     * which is where we will after this file will change to depict this containment of ability and functionaliuty from these 3 types of USERS, customer, sys admin & flight agent.
     * 
     */

    
    //intended to be used by FLIGHT AGENT
    public static void addFlight(Flight flight) {
    if (flight == null) {
        throw new IllegalArgumentException("flight cannot be null");
    }

    String sql =
        "INSERT INTO flight (" +
        "  flight_id, airline_id, airplane_id, origin, destination, date, " +
        "  total_seats, available_seats, price" +
        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
        "ON CONFLICT(flight_id) DO UPDATE SET " +
        "  airline_id      = excluded.airline_id, " +
        "  airplane_id     = excluded.airplane_id, " +
        "  origin          = excluded.origin, " +
        "  destination     = excluded.destination, " +
        "  date            = excluded.date, " +
        "  total_seats     = excluded.total_seats, " +
        "  available_seats = excluded.available_seats, " +
        "  price           = excluded.price";

    PreparedStatement stmt = DB.prepare(sql);

    DB.set(stmt, 1, flight.getFlightId());
    DB.set(stmt, 2, flight.getAirline().getAirlineId());
    DB.set(stmt, 3, flight.getAirplane().getAirplaneId());
    DB.set(stmt, 4, flight.getOrigin());
    DB.set(stmt, 5, flight.getDestination());
    DB.set(stmt, 6, flight.getDate().toString());

    //pulling from our getters in the entities
    int totalSeats     = flight.getAirplane().getCapacity();
    int availableSeats = flight.getAirplane().getCapacity(); //to start this is of course the same but once we get to booking we will modify this

    //setting this to the correct types (REFERENCE TO CHATGPT FOR help  and how to do this without errors)
    DB.set(stmt, 7, Integer.toString(totalSeats));
    DB.set(stmt, 8, Integer.toString(availableSeats));
    DB.set(stmt, 9, Double.toString(flight.getPrice()));

    DB.update(stmt);
}

    // Delete a flight, again intened to be done by flight agenet
    public static void deleteFlight(String flightId) {
        if (flightId == null || flightId.isBlank()) {
            throw new IllegalArgumentException("flightId is required");
        }

        String sql = "DELETE FROM flight WHERE flight_id = ?";

        PreparedStatement stmt = DB.prepare(sql);
        DB.set(stmt, 1, flightId);

        DB.update(stmt);
    }

    //Okay just realized that for reservation we want to connect this from our database to JAVA world for our Customer
    //As the two instance of information really exist in our database under Flight and the Customer ID and both as foreign keys
    // in reservation table as foreign keys, but now we want to build the Java object so our reservation can hold the Flight and not just the ID
    // took a lot of brainstorming and help from ChatGPT to fully understand this, but no confident that this the best way
    //definately learned something new and important here connecting our DB to the JAVA world.
    
    //We had two options do this in ReservationCrud, but I believe that instead this should belong here under Flight and be
    //accessed through our method below by reservationCrud as this logic is most similar (very very simalar) atleast how we implemented it
    //with Search Flights, just now writing to our database this reservation.
    
    //Our truth will always be in the database, thats how conceptually we wanted it so we may have more than one object of the "same flight" in the
    //Java world but thats just for GUI, we will directly referenc eour IDs and create new objects

    //HINT, the reason I was so worried about this was in my design in getcapacity for seeing if there is room on a flight to book
    //however we just need to chekc database pull from there and build a new flight object during that check based on the updated database for that customer
    //and of course apply our control functions to check and thenw we shall no.

    //Big conceptual development for me that I am proud I worked through with of course help from COURSE CONTENT and CHATGPT questions.

    //THIS WILL BE USED BY RESERVATION CRUD TO have the FLIGHT OBJECT needed at time of booking to store which will include the capacity!! Key
    //and passed there for handling and reservation and of course writing to database and updating fields
    
    public static Flight findFlightById(String flightId) {
        
        //quick check and error handling basic and same as before
        if (flightId == null || flightId.isBlank()) {
        throw new IllegalArgumentException("flightId is required");
        }

        //now accessing our database
        try {
            String sql =
                "SELECT " +
                "  f.flight_id      AS id, " +
                "  f.origin         AS origin, " +
                "  f.destination    AS destination, " +
                "  f.date           AS date, " +
                "  a.airline_id     AS airline_id, " +
                "  a.name           AS airline, " +
                "  ap.airplane_id   AS airplane_id, " +
                "  ap.model         AS airplane_model, " +
                "  ap.manufacturer  AS airplane_manufacturer, " +
                "  ap.capacity      AS airplane_capacity, " +
                "  f.price          AS price " +
                "FROM flight f " +
                "JOIN airline  a  ON f.airline_id  = a.airline_id " +
                "JOIN airplane ap ON f.airplane_id = ap.airplane_id " +
                "WHERE f.flight_id = ?";

            PreparedStatement stmt = DB.prepare(sql);
            DB.set(stmt, 1, flightId.trim());

            ResultSet rs = DB.query(stmt);
            if (!DB.next(rs)) {
                return null; // not found
            }

            // This is almost identical to our seach flights so was just copied there
            String id            = DB.getString(rs, "id");
            String dbOrigin      = DB.getString(rs, "origin");
            String dbDestination = DB.getString(rs, "destination");
            String dbDate        = DB.getString(rs, "date");
            String airlineName   = DB.getString(rs, "airline");
            String airlineId     = DB.getString(rs, "airline_id");

            String airplaneId       = DB.getString(rs, "airplane_id");
            String airplaneModel    = DB.getString(rs, "airplane_model");
            String airplaneMfg      = DB.getString(rs, "airplane_manufacturer");
            String airplaneCapStr   = DB.getString(rs, "airplane_capacity");
            String priceStr         = DB.getString(rs, "price");

            double price = 0.0;
            if (priceStr != null && !priceStr.isBlank()) {
                try { price = Double.parseDouble(priceStr); } catch (NumberFormatException ignored) {}
            }

            //Reference to CHATGOT for this function to help, we made some mistakes with type and this is error handling
            int airplaneCapacity = 0;
            if (airplaneCapStr != null && !airplaneCapStr.isBlank()) {
                try { airplaneCapacity = Integer.parseInt(airplaneCapStr); }
                catch (NumberFormatException ignored) { airplaneCapacity = 150; }
            }

            LocalDate departureDate = null;
            if (dbDate != null && !dbDate.isBlank()) {
                departureDate = LocalDate.parse(dbDate);
            }

            Airline airline = new Airline(airlineId, airlineName);
            Airplane airplane = new Airplane(airplaneId, airplaneModel, airplaneMfg, airplaneCapacity);

            return new Flight(
                    id,
                    airline,
                    dbOrigin,
                    dbDestination,
                    departureDate,
                    airplane,
                    price
            );

            //CHATGPT shoutout still have so much troubl implementing this without errors and helped get this going
        } catch (RuntimeException e) {
            System.err.println("Error in findFlightById: " + e.getMessage());
            throw e;
        }
    }
}

