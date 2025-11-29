package src.controllers;

import app.AppContext;
import src.database.RepositoryBridge;
import src.events.ControllerBus;
import src.events.ControllerBus.EventType;
import src.events.ControllerExceptions;
import src.models.User;

//handles authentication logic
public class AuthController {
    private final RepositoryBridge repo;
    private User currentUser;

    public AuthController() {
        this.repo = AppContext.getInstance().repository();
        this.currentUser = null;
    }

    public User login(String email, String password) {
        //checking the inputs first
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email cannot be blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password cannot be blank");
        }

        //asking the repo to find the user
        User user = repo.findUserByEmail(email);
        if (user == null) {
            throw new ControllerExceptions.ControllerException(
                ControllerExceptions.ErrorCode.INVALID_CREDENTIALS,
                "user not found"
            );
        }

        this.currentUser = user;
        //telling everyone the user logged in
        ControllerBus.getInstance().publish(EventType.USER_LOGGED_IN, user);
        return user;
    }

    public void logout() {
        //clearing the session
        if (currentUser != null) {
            ControllerBus.getInstance().publish(EventType.USER_LOGGED_OUT, currentUser);
            currentUser = null;
        }
        //todo clear session in AppContext if session tracking exists
    }

    public User getCurrentUser() {
        return currentUser;
    }

    //todo password policy checks and session management
}

