package controller;

import app.AppContext;
import domain.Flight;
import controller.ControllerBus.EventType;

//handles admin flight management
public class AdminFlightController {
    private final RepositoryBridge repo;

    public AdminFlightController(){
        this.repo=AppContext.getInstance().repository();
    }

    public Flight createOrUpdateFlight(String flightId,String origin,String destination,double price){
        //creating or updating a flight
        if(flightId==null || origin==null || destination==null){
            throw new IllegalArgumentException("flight id, origin, and destination required");
        }
        if(price<0){
            throw new IllegalArgumentException("price cannot be negative");
        }

        //minimal checks are done
        //todo create Flight entity with proper constructor
        //todo save via repo
        //todo publish event
        return null;
    }

    public void cancelFlight(String flightId){
        //cancelling the flight with cascade note
        if(flightId==null || flightId.isBlank()){
            throw new IllegalArgumentException("flightId required");
        }
        //todo find flight and mark as cancelled
        //todo handle cascade and notify affected reservations
        //GUI should handle messaging
    }

    //todo cascade rules and reservation notifications
}

