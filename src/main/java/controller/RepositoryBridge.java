package controller;

import java.util.List;
import domain.User;
import domain.Flight;
import domain.Customer;
import domain.FlightCustomerReservation;

//thin wrapper over the database layer
public class RepositoryBridge {
    //todo wrap existing database access like userCRUD
    //this is just a facade for now

    public User findUserByEmail(String email){
        //delegating to existing userCRUD or similar
        //todo wire to src.database.userCRUD or create flight/user repos
        return null;
    }

    public List<Flight> searchFlights(String origin,String destination,String date){
        //delegating to flight repo if it exists
        //todo implement flight search
        return List.of();
    }

    public Flight findFlightById(String flightId){
        //finding a single flight
        //todo implement
        return null;
    }

    public void saveReservation(FlightCustomerReservation reservation){
        //saving the reservation
        //todo implement
    }

    public List<FlightCustomerReservation> findReservationsByUserId(String userId){
        //getting all reservations for a user
        //todo implement
        return List.of();
    }

    //todo add more repo methods as needed
}

