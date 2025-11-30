package src.actions;

/**
 * Actions that an administrator-facing view can trigger. This extends the
 * shared {@link UserActions} contract so admins automatically gain
 * access to common capabilities such as flight search, while adding
 * admin-only navigation and tools.
 */
public interface AdminActions extends UserActions {

    /**
     * Navigate from the admin dashboard to the flight editing workspace.
     * The concrete implementation lives in the top-level {@code AppController},
     * which decides which panel to show.
     */
    void showAdminFlightEditor();
}



