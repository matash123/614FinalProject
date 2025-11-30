package src.actions;

import java.time.LocalDate;
import java.util.List;
import src.models.Airline;
import src.models.Airplane;
import src.models.Flight;

/**
 * Actions available to an agent/flight-management style UI. This extends
 * {@link UserActions} so agents share the same core capabilities (like
 * flight search) while adding management-specific operations.
 */
public interface AgentActions extends UserActions {

    /**
     * Load all airlines for use in agent/admin UIs.
     */
    List<Airline> loadAllAirlines();

    /**
     * Load all airplanes for use in agent/admin UIs.
     */
    List<Airplane> loadAllAirplanes();

    /**
     * Create or update a flight from the agent console.
     */
    Flight agentSaveFlight(
        String flightId,
        Airline airline,
        Airplane airplane,
        String origin,
        String destination,
        LocalDate date,
        double price
    );

    /**
     * Delete/cancel a flight from the agent console.
     */
    void agentDeleteFlight(String flightId);
}


