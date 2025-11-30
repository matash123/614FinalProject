package src.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import src.AppFrame;
import src.DTO.FlightDTO;
import src.actions.AppActions;
import src.config.Theme;
import src.factory.ControllerFactory;
import src.models.Airline;
import src.models.Airplane;
import src.models.Flight;
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
    private final AdminFlightController adminFlightController = new AdminFlightController();

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

    }


    public void onLoginSuccess(User user) {
        if (mf == null || user == null) {
            return;
        }

        String role = user.getRole();
        if ("AGENT".equalsIgnoreCase(role)) {
            mf.setView(mf.makeAgentPanel(this));
        } else if ("ADMIN".equalsIgnoreCase(role)) {
            mf.setView(mf.makeAdminPanel(this));
        } else {
            // Default to customer experience for now.
            mf.setView(mf.makeCustomerPanel(this));
        }
        mf.applyThemeToUI(this.theme);
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
    public List<FlightDTO> searchFlights(String origin, String destination, String startDate, String id) {
        // For now, FlightSearchController supports a single departure date.
        String dateFilter = (startDate != null && !startDate.isBlank()) ? startDate : null;
        String idFilter = (id != null && !id.isBlank()) ? id : null;

        List<Flight> flights = flightSearchController.searchFlights(origin, destination, dateFilter, idFilter);

        // Convert domain models to DTOs for the UI.
        return flights.stream()
            .map(FlightDTO::fromModel)
            .collect(Collectors.toList());
    }

    // ------------------------------------------------------------
    // AGENT / ADMIN FLIGHT MANAGEMENT
    // ------------------------------------------------------------

    @Override
    public List<Airline> loadAllAirlines() {
        return adminFlightController.getAllAirlines();
    }

    @Override
    public List<Airplane> loadAllAirplanes() {
        return adminFlightController.getAllAirplanes();
    }

    public Flight agentSaveFlight(
        String flightId,
        Airline airline,
        Airplane airplane,
        String origin,
        String destination,
        LocalDate date,
        double price
    ) {
        return adminFlightController.createOrUpdateFlight(
            flightId,
            airline,
            airplane,
            origin,
            destination,
            date,
            price
        );
    }

    @Override
    public void agentDeleteFlight(String flightId) {
        adminFlightController.cancelFlight(flightId);
    }

    // ------------------------------------------------------------
    // AGENT BOOKING / RESERVATION EDITING NAVIGATION
    // ------------------------------------------------------------
    @Override
    public void showAgentBookingEditor() {
        if (mf == null) {
            return;
        }
        mf.setView(new src.views.AgentBookingEditorPanel(this));
        mf.applyThemeToUI(this.theme);
    }

    @Override
    public void showAdminFlightEditor() {
        if (mf == null) {
            return;
        }
        mf.setView(new src.views.AdminFlightEditorPanel(this));
        mf.applyThemeToUI(this.theme);
    }

}
