package src;

import src.schemas.loginResult;

/**
 * High-level actions that the UI can invoke. Implemented by AppController,
 * which then delegates to more specific sub-controllers.
 */
public interface AppActions {
    loginResult onLoginAttempt(String username, String password);
    void switchTheme();

    /**
     * Trigger a flight search initiated from the customer UI.
     * Concrete controllers are responsible for fetching results and
     * notifying interested views (e.g., via ControllerBus events).
     */
    void searchFlights(String origin, String destination, String startDate, String endDate);
}
