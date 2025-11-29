package src.controllers;

import src.AppActions;
import src.AppFrame;
import src.config.Theme;
import src.factory.ControllerFactory;
import src.models.User;
import src.schemas.loginResult;

/**
 * Top-level application controller that receives high-level UI events
 * and delegates the work to more focused sub-controllers.
 */
public class AppController implements AppActions {
    private Theme theme;
    private AppFrame mf;
    private final UserController userController = new UserController();
    private final src.controllers.FlightSearchController flightSearchController;

    public  AppController(Theme t){
        this.theme = t;
        // get the shared flight search controller from the factory
        this.flightSearchController = ControllerFactory.getInstance().flights();
    }


    public void setMainFrame(AppFrame frame) {
        this.mf = frame;
    }

    public void start() {
        mf.setView(mf.makeLoginPanel(this));
        mf.applyThemeToUI(this.theme);
    }

    // ------------------------------------------------------------
    // LOGIN SYSTEM
    // ------------------------------------------------------------
    @Override
    public loginResult onLoginAttempt(String username, String password) {
        loginResult rslt = userController.attemptLogin(username, password);
        if(rslt.success()){
            onLoginSuccess(rslt.user());
        }
        return rslt;

        // deal with user input before actually loggin in through controller.
        return userContoler.attemptLogin(username, password);

    }


    public void onLoginSuccess(User user) {
        //set active user as user;
        mf.setView(mf.makeCustomerPanel(this));
        mf.applyThemeToUI(this.theme);
        // switch (user.getRole()) {
        //     case "CUSTOMER" -> view.showCustomer();
        //     case "AGENT"    -> view.showAgent();
        //     case "ADMIN"    -> view.showAdmin();
        // }
    }

    // ------------------------------------------------------------
    // THEME SYSTEM
    // ------------------------------------------------------------
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

    // ------------------------------------------------------------
    // FLIGHT SEARCH: delegate from UI to FlightSearchController
    // ------------------------------------------------------------
    @Override
    public void searchFlights(String origin, String destination, String startDate, String endDate) {
        // For now, FlightSearchController supports a single departure date.
        String dateFilter = (startDate != null && !startDate.isBlank()) ? startDate : null;
        flightSearchController.searchFlights(origin, destination, dateFilter);
    }

}
