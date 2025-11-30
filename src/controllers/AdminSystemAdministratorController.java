package src.controllers;

import java.util.List;
import src.database.AirlineCrud;
import src.database.AirplaneCrud;
import src.models.Airline;
import src.models.Airplane;

/**
 * Controller for our System Administrator, this one is going to be again now executing how we set up the CRUD
 * which will be that this user is the one who can create and edit airlines and airplanes, add key dependencie for Flight Agent and
 * executes the functionality and encapsulation that we as a team intended during our planning and diagram stage.
 * 
 * 2 MAIN PARTS TO THIS CLASS, which reflectes SYS ADMIN control
 * AIRPLANE AND AIRLINE MANAGEMENT!!!,
 * 
 * Coding was quite straightforward now that we have a great understanding of our DB and just finished CRUD everything was fresh and flowy
 * Reused code between AIRPLANE AND AIRLINE
 * 
 * Reference: ChatGPT for debugging and analogous coding examples which helped implementation (especially with error handling)
 */

public class AdminSystemAdministratorController {
    //getting all the airlines
    public List<Airline> getAllAirlines() {
        return AirlineCrud.findAll();
    }

    // Method to create and update and airline
    public Airline createOrUpdateAirline(String airlineId, String name) {
        if (airlineId == null || airlineId.isBlank()) {
            throw new IllegalArgumentException("airlineId is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("airline name is required");
        }

        Airline airline = new Airline(airlineId.trim(), name.trim());
        AirlineCrud.saveAirline(airline);
        return airline;
    }

    // Deleting an airline, quite straightforward and done by airlineID
    public void deleteAirline(String airlineId) {
        if (airlineId == null || airlineId.isBlank()) {
            throw new IllegalArgumentException("airlineId is required");
        }
        AirlineCrud.deleteAirline(airlineId.trim());
    }

   
    //loading all airplanes in database, would certainly be usesful to see
    public List<Airplane> getAllAirplanes() {
        return AirplaneCrud.findAll();
    }

    /**
     *AIRPLANE PORTION, very similar as airline just for airplane, but of course KEY DIFFERNCE:
     // Airplane is owned by airline (dependent so takes it in), and that this key as we have aggregation
     */
    public Airplane createOrUpdateAirplane(String airplaneId, Airline airline, String model, String manufacturer, int capacity) {

        if (airplaneId == null || airplaneId.isBlank()) {
            throw new IllegalArgumentException("airplaneId is required");
        }
        if (airline == null) {
            throw new IllegalArgumentException("airline is required (airplane must belong to an airline)");
        }
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("model is required");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }

        Airplane airplane = new Airplane(
                airplaneId.trim(),
                model.trim(),
                manufacturer != null ? manufacturer.trim() : "",
                capacity
        );

        // tie airplane to specific airline it belongs to and we of course need airlineId as this how its portrayed in the DB
        AirplaneCrud.addAirplane(airplane, airline.getAirlineId()); //using getter to get the airline
        return airplane;
    }

    //Delete a specific airplane (admin selects row and hits "Delete Airplane")
    public void deleteAirplane(String airplaneId) {
        if (airplaneId == null || airplaneId.isBlank()) {
            throw new IllegalArgumentException("airplaneId is required");
        }
        AirplaneCrud.deleteAirplane(airplaneId.trim());
    }
}

