package src.actions;

import java.util.List;
import src.DTO.FlightDTO;

/**
 * Shared actions available to any authenticated user regardless of role
 * (customer, agent, admin, etc.). This keeps cross-cutting capabilities
 * like flight search defined in a single place.
 */
public interface UserActions {

    /**
     * Trigger a generic flight search.
     *
     * All GUI callers send their intent through this method on the
     * AppController (via the {@link AppActions} facade), which in turn
     * delegates to the domain controllers and returns matching flights as
     * DTOs ready for display.
     *
     * For now, {@code endDate} may be unused by some implementations but is
     * included to support flexible date-range searches later.
     */
    List<FlightDTO> searchFlights(String origin, String destination, String startDate, String id);
}


