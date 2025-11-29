package src.controllers;

import java.time.LocalDate;
import java.util.List;
import src.database.RepositoryBridge;
import src.models.Airline;
import src.models.Airplane;
import src.models.Flight;

//handles admin flight management -> FLIGHT AGENT CONTROLLER ESSENTIALLY, SO WE SHOULD RENAME THIS CONTROLLER I BELEIVE

public class AdminFlightController {
    private final RepositoryBridge repo;

    public AdminFlightController() {
        this.repo = AppContext.getInstance().repository();
    }

    //These are our airline Getters so we have access here under Flight Agent
    // Will be criticsl for booking, but whats key are these are READ ONLY
    //This is great as it follows encapsulation and extends functionality in meaningful and deliberate way
    public List<Airline> getAllAirlines() {
        return repo.findAllAirlines();
    }

    public List<Airplane> getAllAirplanes() {
        return repo.findAllAirplanes();
    }

    public Flight createOrUpdateFlight(String flightId, Airline airline,Airplane airplane, String origin,String destination,LocalDate date, double price) {

        //basic checks to ensure that constructor is properly filled out, I think this is imp, especially when working with a real flight. FLIGHT AGENT shouldnt be able to make mistakes (grave consequences)
        if (flightId == null || flightId.isBlank()) {
            throw new IllegalArgumentException("flightId is required");
        }
        if (airline == null) {
            throw new IllegalArgumentException("airline is required");
        }
        if (airplane == null) {
            throw new IllegalArgumentException("airplane is required");
        }
        if (origin == null || origin.isBlank()) {
            throw new IllegalArgumentException("origin is required");
        }
        if (destination == null || destination.isBlank()) {
            throw new IllegalArgumentException("destination is required");
        }
        if (date == null) {
            throw new IllegalArgumentException("date is required");
        }
        if (price < 0) {
            throw new IllegalArgumentException("price cannot be negative");
        }

        // Flight constructor
        // and uses airplane.getCapacity() for total/available seats.
        Flight flight = new Flight(flightId,airline,origin,destination,date,airplane,price
        );

        //saving to repo, which is great
        repo.addFlight(flight);

        //TO DO: event bus handling and how we want to send messages, like we delete ariline, flight should be modified as well

        return flight;
    }

    public void cancelFlight(String flightId) {
        //cancelling the flight with cascade note
        if (flightId == null || flightId.isBlank()) {
            throw new IllegalArgumentException("flightId required");
        }

        repo.deleteFlight(flightId);
        //todo find flight and mark as cancelled
        //todo handle cascade and notify affected reservations
        //GUI should handle messaging
    }

    //todo cascade rules and reservation notifications
}

