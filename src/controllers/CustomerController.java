package src.controllers;

import java.util.List;
import src.database.ReservationCRUD;
import src.factory.ControllerFactory;
import src.models.Reservation;
import src.models.User;

//handles customer specific operations
public class CustomerController {
    public void updateProfile(String userId, String name, String email, String phone) {
        //updating the customer profile
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId required");
        }
        
        //get user from user controller
        UserController userController = ControllerFactory.getInstance().user();
        User currentUser = userController.getCurrentUser();
        if (currentUser == null || !currentUser.getUserId().equals(userId)) {
            throw new IllegalArgumentException("unauthorized: can only update own profile");
        }
        
        //todo implement customer profile update via RepositoryBridge
    }

    public List<Reservation> getReservations(String userId) {
        //getting all reservations for this user
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId required");
        }
        return ReservationCRUD.findByUserId(userId);
    }

}

