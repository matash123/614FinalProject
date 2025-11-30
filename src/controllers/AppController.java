package src.controllers;

import src.AppFrame;
import src.config.Theme;
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

    public  AppController(Theme t){
        this.theme = t;
    }


    public void setMainFrame(AppFrame frame) {
        this.mf = frame;
    }

    public void start() {
        // At startup we only care about wiring the login view. The concrete
        // user controller is repsonsible for checking the user credentials and logging in by setting the current user as its user attribute. 
        // then the the component will call the onLoginSuccess method to update the UI.
        mf.setView(mf.makeLoginPanel(this));
        mf.applyThemeToUI(this.theme);
    }

    // ------------------------------------------------------------
    // LOGIN SYSTEM
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
