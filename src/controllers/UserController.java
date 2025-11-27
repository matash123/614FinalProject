package src.controllers;

import src.models.User;
import src.database.userCRUD;
import src.schemas.loginResult;

public class UserController {
    public UserController(){};

    public loginResult attemptLogin(String username, String password) {
        User usr = userCRUD.getUser(username);

        if (usr == null)
            return new loginResult(false, "Unknown username", null);

        if (!usr.checkPassword(password))
            return new loginResult(false, "Invalid password", null);

        return new loginResult(true, "", usr);
    }
}

