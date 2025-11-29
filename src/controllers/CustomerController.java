package src.controllers;

import java.util.List;
import src.database.RepositoryBridge;
import src.models.FlightCustomerReservation;

//handles customer specific operations
public class CustomerController {
    private final RepositoryBridge repo;

    public CustomerController() {
        this.repo = AppContext.getInstance().repository();
    }

    public void updateProfile(String userId, String name, String email, String phone) {
        //updating the customer profile
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId required");
        }
        //todo fetch customer update fields and save
        //todo update profile validation
    }

    public List<FlightCustomerReservation> getReservations(String userId) {
        //getting all reservations for this user
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId required");
        }
        return repo.findReservationsByUserId(userId);
    }

    //todo update profile validation and error handling
}

