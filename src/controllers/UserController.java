package src.controllers;

import src.database.userCRUD;
import src.models.User;
import src.schemas.loginResult;

public class UserController {
    public UserController(){};
    public User user;

    public User getCurrentUser() {
        return user;
    }

    public loginResult attemptLogin(String username, String password) {
        System.out.println(username);
        User usr = userCRUD.getUser(username);

        if (usr == null)
            return new loginResult(false, "Unknown username", null);

        if (!usr.checkPassword(password))
            return new loginResult(false, "Invalid password", null);

        this.user = usr;
        return new loginResult(true, "", usr);
    }

    /**
     * Register a new customer user with the given username and password.
     */
    public loginResult registerCustomer(String username, String password) {
        try {
            User created = userCRUD.createCustomer(username, password);
            this.user = created;
            return new loginResult(true, "Account created. You can now log in.", created);
        } catch (IllegalArgumentException ex) {
            return new loginResult(false, ex.getMessage(), null);
        } catch (RuntimeException ex) {
            return new loginResult(false, "Could not create user: " + ex.getMessage(), null);
        }
    }
}

