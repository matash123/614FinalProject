package src.controllers;

import src.AppFrame;
import src.config.Theme;
import src.factory.ControllerFactory;
import src.models.User;

/**
 * Top-level application controller that receives high-level UI/UX events
 * (navigation, theme changes, role-based view switching) and delegates
 * domain logic to more focused sub-controllers.
 *
 * IMPORTANT: This controller no longer performs authentication itself.
 * Components such as {@code LoginPanel} call {@link src.controllers.UserController}
 * directly to validate credentials and then invoke {@link #onLoginSuccess(User)}
 * on success.
 */
public class AppController {
    private Theme theme;
    private AppFrame mf;
    private static AppController INSTANCE;

    public AppController(Theme t){
        this.theme = t;
        INSTANCE = this;
    }

    public void setMainFrame(AppFrame frame) {
        this.mf = frame;
    }

    public static AppController getInstance() {
        return INSTANCE;
    }

    public void start() {
        // At startup we only care about wiring the login view. The concrete
        // user controller is repsonsible for checking the user credentials and logging in by setting the current user as its user attribute. 
        // then the the component will call the onLoginSuccess method to update the UI.
        mf.setView(mf.makeLoginPanel(this));
        mf.applyThemeToUI(this.theme);
    }

    /**
     * Expose the current theme so views/components can query it if needed.
     */
    public Theme getTheme() {
        return theme;
    }

    // ------------------------------------------------------------
    // LOGIN / VIEW SELECTION
    // ------------------------------------------------------------
    public void onLoginSuccess(User user) {
        if (mf == null || user == null) {
            return;
        }

        String role = user.getRole();
        if ("AGENT".equalsIgnoreCase(role)) {
            mf.setView(mf.makeAgentPanel());
        } else if ("ADMIN".equalsIgnoreCase(role)) {
            mf.setView(mf.makeAdminPanel());
        } else {
            // Default to customer experience for now.
            mf.setView(mf.makeCustomerPanel());
        }
        mf.applyThemeToUI(this.theme);
        // Ensure the new main view initializes its header and data from controllers.
        updateAppView();
    }

    /**
     * Log the current user out and return to the login screen.
     * Clears the current user in {@link src.controllers.UserController}
     * and resets the main frame view to the login panel.
     */
    public void logout() {
        // Clear current user in the dedicated user controller.
        ControllerFactory.getInstance().user().clearCurrentUser();

        if (mf == null) {
            return;
        }

        // Return to the login panel.
        mf.setView(mf.makeLoginPanel(this));
        mf.applyThemeToUI(this.theme);
    }

    /**
     * Ask the current main view to refresh its data in-place.
     * This does NOT recreate the panel; instead panels are responsible
     * for updating their own child components (e.g., booking lists).
     */
    public void updateAppView() {
        if (mf == null) {
            return;
        }
        mf.updateCurrentView();
    }

    // ------------------------------------------------------------
    // THEME SYSTEM
    // ------------------------------------------------------------
    public void switchTheme() {
        if(theme.name.equals("LIGHT")){
            this.theme = Theme.DARK;
        } else{
            this.theme = Theme.LIGHT;
        }
        if (mf != null) {
            mf.applyThemeToUI(this.theme);
        }
    }
}
