package src.controllers;

import src.AppActions;
import src.AppFrame;
import src.config.Theme;
import src.models.User;
import src.schemas.loginResult;


public class AppController implements AppActions {
    private Theme theme;
    private AppFrame mf;
    private final UserController userController = new UserController();

    public  AppController(Theme t){
        this.theme = t;
    }


    public void setMainFrame(AppFrame frame) {
        this.mf = frame;
    }

    public void start() {
        mf.setView(mf.makeLoginPanel(this));
        mf.applyThemeToUI(this.theme);
    }

//LOGIN SYSTEM
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
    //mf.showCustomer();
    // switch (user.getRole()) {
    //     case "CUSTOMER" -> view.showCustomer();
    //     case "AGENT"    -> view.showAgent();
    //     case "ADMIN"    -> view.showAdmin();
    // }
}

//THEME SYSTEM
@Override
public void switchTheme() {
    if(theme.name.equals("LIGHT")){
        this.theme = Theme.DARK;
    } else{
        this.theme = Theme.LIGHT;
    };
    if (mf != null) {
        mf.applyThemeToUI(this.theme);
    }
}

}
