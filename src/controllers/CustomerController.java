package src.controllers;

import app.AppContext;
import java.util.List;
import src.database.RepositoryBridge;
import src.models.FlightCustomerReservation;
import src.models.User;

//handles customer specific operations
public class CustomerController {
    private final RepositoryBridge repo;
    private final AuthController authController;

    public CustomerController() {
        this.repo = AppContext.getInstance().repository();
        this.authController = new AuthController();
    }

    /**
     * Updates customer profile information.
     * Only the current user can update their own profile.
     */
    public void updateProfile(String userId, String name, String email, String phone) {
        //updating the customer profile
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId required");
        }
        
        //authorization check - only current user can update own profile
        User currentUser = authController.getCurrentUser();
        if (currentUser == null || !currentUser.getUserId().equals(userId)) {
            throw new IllegalArgumentException("unauthorized: can only update own profile");
        }
        
        //todo implement customer profile update via RepositoryBridge
    }

    public List<FlightCustomerReservation> getReservations(String userId) {
        //getting all reservations for this user
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId required");
        }
        return repo.findReservationsByUserId(userId);
    }

}

