package src.views;

/**
 * Simple controller for switching the active "page" (center content)
 * inside a dashboard-style panel (customer, agent, admin).
 *
 * User views create an instance and pass it down to their active
 * sub-panels so those panels can request page changes without needing
 * to know about the concrete parent implementation.
 */
public interface PageController {

    /**
     * Make the given panel the active content area.
     */
    void show(DynamicPanel panel);
}

