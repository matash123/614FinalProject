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
    public static List<Flight> searchFlights(String origin, String destination, String date) {
        List<Flight> flights = new ArrayList<>();

        try {

            //CREATE SQL QUERY (now fixed with correct matching our SCHEMA so this way we are reaching our DATABASe), and connected to Airline
            // I was missing airplane table, which now created and joined in our search - my bad
            StringBuilder sql = new StringBuilder(
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
                sql.append(" AND date = ?");
                params.add(date.trim());
            }

            PreparedStatement stmt = DB.prepare(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                DB.set(stmt, i + 1, params.get(i));
            }

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
    
}

