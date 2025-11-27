package src.controllers;

import src.AppActions;
import src.AppView;
import src.models.User;
import src.schemas.loginResult;
import src.database.userCRUD;
import src.controllers.*;



public class AppController implements AppActions {

    private AppView view;
    private final UserController userController = new UserController();


    public void setView(AppView view) {
        this.view = view;
    }

    public void start() {
        view.showLogin();
    }

@Override
public loginResult onLoginAttempt(String username, String password) {
    loginResult rslt = userController.attemptLogin(username, password);
    if(rslt.success()){
        onLoginSuccess(rslt.user());
    }
    return rslt;
}


public void onLoginSuccess(User user) {
    //set active user as user;

    switch (user.getRole()) {
        case "CUSTOMER" -> view.showCustomer();
        case "AGENT"    -> view.showAgent();
        case "ADMIN"    -> view.showAdmin();
    }
}


}
