package src.controllers;

import java.time.LocalDate;
import java.util.List;
import src.database.AirlineCRUD;
import src.database.AirplaneCRUD;
import src.database.FlightCrud;
import src.events.ControllerBus;
import src.models.Airline;
import src.models.Airplane;
import src.models.Flight;

//handles admin flight management -> FLIGHT AGENT CONTROLLER ESSENTIALLY, SO WE SHOULD RENAME THIS CONTROLLER I BELEIVE

public class AdminFlightController {

    //These are our airline Getters so we have access here under Flight Agent
    // Will be criticsl for booking, but whats key are these are READ ONLY
    //This is great as it follows encapsulation and extends functionality in meaningful and deliberate way
    public List<Airline> getAllAirlines() {
        return AirlineCRUD.findAll();
    }

    public List<Airplane> getAllAirplanes() {
        return AirplaneCRUD.findAll();
    }

    public Flight createOrUpdateFlight(String flightId, Airline airline,Airplane airplane, String origin,String destination,LocalDate date, double price) {

        //basic checks to ensure that constructor is properly filled out, I think this is imp, especially when working with a real flight. FLIGHT AGENT shouldnt be able to make mistakes (grave consequences)
        if (airline == null) {
            throw new IllegalArgumentException("airline is required");
        }

        // If no ID was provided (creating a brand new flight), generate a unique one.
        if (flightId == null || flightId.isBlank()) {
            flightId = generateFlightId(airline, date);
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
        Flight flight = new Flight(flightId,airline,origin,destination,date,airplane,price);

        //saving using FlightCrud
        FlightCrud.addFlight(flight);

        //publishing flight created/updated event
        //using FLIGHTS_LOADED to notify that flights have changed
        ControllerBus.getInstance().publish(ControllerBus.EventType.FLIGHTS_LOADED, List.of(flight));

        return flight;
    }

    /**
     * Generate a reasonably unique flight ID based on airline and date.
     * The exact format is not user-facing and is intended for internal use.
     */
    private String generateFlightId(Airline airline, LocalDate date) {
        StringBuilder sb = new StringBuilder();
        if (airline != null && airline.getAirlineId() != null && !airline.getAirlineId().isBlank()) {
            sb.append(airline.getAirlineId());
        } else {
            sb.append("FL");
        }
        if (date != null) {
            sb.append(date.toString().replace("-", ""));
        }
        sb.append("-").append(System.currentTimeMillis());
        return sb.toString();
    }

    public void cancelFlight(String flightId) {
        //cancelling the flight with cascade note
        if (flightId == null || flightId.isBlank()) {
            throw new IllegalArgumentException("flightId required");
        }

        FlightCrud.deleteFlight(flightId);
        //todo find flight and mark as cancelled
        //todo handle cascade and notify affected reservations
        //GUI should handle messaging
    }

    //nice callable functions for admin/agent pages

    //create a new flight
    public Flight createFlight(String flightId, Airline airline, Airplane airplane, String origin, String destination, LocalDate date, double price) {
        return createOrUpdateFlight(flightId, airline, airplane, origin, destination, date, price);
    }

    //update existing flight
    public Flight updateFlight(String flightId, Airline airline, Airplane airplane, String origin, String destination, LocalDate date, double price) {
        return createOrUpdateFlight(flightId, airline, airplane, origin, destination, date, price);
    }

    //delete a flight
    public void deleteFlight(String flightId) {
        cancelFlight(flightId);
    }

    //publish flight (make it available for booking)
    public boolean publishFlight(String flightId) {
        //for now just verify flight exists
        Flight f = FlightCrud.findFlightById(flightId);
        if (f == null) {
            return false;
        }
        //todo add published flag to flight model/db if needed
        ControllerBus.getInstance().publish(ControllerBus.EventType.FLIGHTS_LOADED, List.of(f));
        return true;
    }

    //archive flight (hide from search but keep in db)
    public boolean archiveFlight(String flightId) {
        //todo implement archive logic if needed
        Flight f = FlightCrud.findFlightById(flightId);
        return f != null;
    }

    //add customer to flight (create reservation)
    public boolean addCustomerToFlight(String customerId, String flightId, int seats) {
        if (customerId == null || flightId == null || seats <= 0) {
            throw new IllegalArgumentException("invalid parameters");
        }
        //todo implement - would need to call BookingController or ReservationCRUD
        //for now return false as placeholder
        return false;
    }

    //remove customer from flight (cancel reservation)
    public boolean removeCustomerFromFlight(String reservationId) {
        if (reservationId == null || reservationId.isBlank()) {
            throw new IllegalArgumentException("reservationId required");
        }
        //todo implement - would need ReservationCRUD.deleteById
        return false;
    }

    //list flights with optional filters
    public List<Flight> listFlights(String origin, String destination, String date) {
        return FlightCrud.searchFlights(origin, destination, date, null);
    }

    //get flight details by id
    public Flight getFlightDetails(String flightId) {
        if (flightId == null || flightId.isBlank()) {
            throw new IllegalArgumentException("flightId required");
        }
        return FlightCrud.findFlightById(flightId);
    }

    //todo add authorization check to verify user is FlightAgent or Admin
}

