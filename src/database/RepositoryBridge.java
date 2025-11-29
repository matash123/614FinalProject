package src.database;

import java.util.List;
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
}

