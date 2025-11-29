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

            //CREATE SQL QUERY
            StringBuilder sql = new StringBuilder(
                "SELECT id, origin, destination, date, airline, price " +
                "FROM flight WHERE 1=1"
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
                String priceStr = DB.getString(rs, "price");

                double price = 0.0;

                //ERROR HANDLING ON PRICE TO NUMBER
                if (priceStr != null && !priceStr.isBlank()) {
                    try {
                        price = Double.parseDouble(priceStr);
                    } catch (NumberFormatException ignored) {}
                }

                //ERROR HANDLING ON DATE
                LocalDate departureDate = null;
                if (dbDate != null && !dbDate.isBlank()) {
                    // expected format: yyyy-MM-dd (matches the UI mask)
                    departureDate = LocalDate.parse(dbDate);
                }

                // TODO REPLACE AND GET ACTUAL AIRLINE, we cant create a flight without creating an airline model. So we must search and create and airline. 
                // minimal Airline / Airplane construction so the Flight model is complete
                String airlineId = (airlineName != null && airlineName.length() >= 3)
                        ? airlineName.substring(0, 3).toUpperCase()
                        : "GEN";
                Airline airline = new Airline(airlineId, airlineName);
                Airplane airplane = new Airplane("GEN-" + id, "Generic", 150);

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
}

