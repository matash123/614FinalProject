package src.models;

import java.time.LocalDate;
import java.util.ArrayList;

/*
* This is a singular class which represents an actual flight it contains most of our classes and is by far our most "Involved" entity class
*/

public class Flight {

    private final String flightId;     // This is our primary key of the Flight
    private Airline airline;           // Each Flight should belong to an airline, this can be removed if we hold an Airpline object which holds an Airpline (should come back to this)
    private String origin;             // City/airport code of departure
    private String destination;        // Destination city/airport code
    private LocalDate date;            // Date of departure
    private int totalSeats;            // Total seats from the assigned airplane
    private int availableSeats;        // This is defaulted to total seats, but can be changed down the road
    private double price;
    private Airplane airplane;         // When we assign a flight an airplane we should be able to populate most infomation from this
    private boolean isFull;


    //This is array is the crux, we can update on how many people and who are on each flight and also know if we have seats available still, I love it
    // However we will need functions, and build in more logic, this may be better served that instead of a reference to an object we just copy over customer ID's and leave this as storage only and to know if seats are
    // available, due to our database may be over kill to double couple these classes (ASK BARRETT)
    
    // Actually NVM maybe just bet to have Reservation link Customer and Flight instead.
    private ArrayList<Customer> customersOnFlight; // Nmy idea is that once a customer is added we add this hear, and will use updates (potentially observer), from there this is great as we can see how many flights are left


    // Constructor using simple origin/destination strings for now.
    public Flight(String flightId,
                  Airline airline,
                  String origin,
                  String destination,
                  LocalDate date,
                  Airplane airplane,
                  double price) {

        this.flightId = flightId;
        this.airline = airline;
        this.origin = origin;
        this.destination = destination;
        this.date = date;
        this.airplane = airplane;
        this.totalSeats = airplane.getCapacity(); // This is good
        this.availableSeats = this.totalSeats; // Initially available seats should be total seats
        this.price = price;
        this.isFull = !hasAvailableSeats();
    }


    // These need updates, this method to update whether or not a flight is full, so we can even book seats

    public boolean hasAvailableSeats(int seatsRequested) {
        return availableSeats >= seatsRequested;
    }

    //secondary method specific for populating our internal variable of isFull so when a flight is full it can be
    //updated on the interface through calling this method/being stored on the databas
    public boolean hasAvailableSeats() {
        return availableSeats > 0;
    }

    
    public void reserveSeats(int seatsRequested) {
        if (!hasAvailableSeats(seatsRequested)) {
            throw new IllegalArgumentException("Not enough seats available");
        }
        availableSeats -= seatsRequested;
        //updating whether or not our flight is now full after adding these seats
        isFull = hasAvailableSeats();

    }

    public void releaseSeats(int seatsToRelease) {

        if (totalSeats > availableSeats + seatsToRelease) {
            throw new IllegalArgumentException("Illegal Number of Seat Released");
        }
        availableSeats = availableSeats + seatsToRelease;
    }

    /**
     * Starting all of our getters which are needed for all our members
     * 
     */
    public String getFlightId() {
        return flightId;
    }

    public Airline getAirline() {
        return airline;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public double getPrice() {
        return price;
    }

    

    // setters only where you expect real changes (e.g., price update)
}
