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
}

