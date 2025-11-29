package src.database;

import java.util.List;
import src.models.Airline;
import src.models.Airplane;
import src.models.Flight;
import src.models.FlightCustomerReservation;
import src.models.User;

//thin wrapper over the database layer
public class RepositoryBridge {
    
    //todo wrap existing database access like userCRUD
    //this is just a facade for now

    public User findUserByEmail(String email) {
        //delegating to existing userCRUD or similar
        //todo wire to src.database.userCRUD or create flight/user repos
        return null;
    }

    public List<Flight> searchFlights(String origin, String destination, String date) {
        // delegate to the flight-specific CRUD layer
        return FlightCrud.searchFlights(origin, destination, date);
    }

    public Flight findFlightById(String flightId) {
        //finding a single flight
        //todo implement
        return null;
    }

    public void saveReservation(FlightCustomerReservation reservation) {
        //saving the reservation
        //todo implement
    }

    public List<FlightCustomerReservation> findReservationsByUserId(String userId) {
        //getting all reservations for a user
        //todo implement
        return List.of();
    }

    //todo add more repo methods as needed

    //ADDED NOW MATCHING OUR CRUD


    //AIRLINES, this function is intened be used by SYS ADMIN
    public void saveAirline(Airline airline) {       
        AirlineCrud.saveAirline(airline);
    }

    // AGAIN SYS ADMIN
    public void deleteAirline(String airlineId) {      
        AirlineCrud.deleteAirline(airlineId);
    }

    //ALL 3 USERS SHOULD USES THIS
    public List<Airline> findAllAirlines() {           
        return AirlineCrud.findAll();
    }

    //AIRPLANES

    //This is again intened by SYS ADMIN
    public void addAirplane(Airplane airplane, String airlineId) {
        AirplaneCrud.addAirplane(airplane, airlineId);
    }

    //again SYTEM ADMIN
    public void deleteAirplane(String airplaneId) {    
        AirplaneCrud.deleteAirplane(airplaneId);
    }

    //ALL 3 users will be able to see airplane, actually maybe custome prolly wont use this, but we will get to that in controller
    public List<Airplane> findAllAirplanes() {        
        return AirplaneCrud.findAll();
    }

    // ADD FLIGHTS, and DELETE, intended to be used and controlled by FLIGHT AGENT in their controller
    public void addFlight(Flight flight) {
        FlightCrud.addFlight(flight);
    }

    public void deleteFlight(String flightId) {
        FlightCrud.deleteFlight(flightId);
    }
}

