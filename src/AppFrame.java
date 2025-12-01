package src;

import src.config.Theme;
import src.controllers.AppController;
import src.views.DynamicPanel;

public interface AppFrame {

    void setView(DynamicPanel p);

    DynamicPanel makeLoginPanel(AppController appController);
    DynamicPanel makeCustomerPanel();
    DynamicPanel makeAgentPanel();
    DynamicPanel makeAdminPanel();
    DynamicPanel makeSignupPanel(AppController appController);

    void applyThemeToUI(Theme t);

    /**
     * Ask the currently active main panel to refresh its data from controllers.
     * Panels are responsible for hard-coding update calls to their child
     * components (e.g., header widgets, booking lists).
     */
    void updateCurrentView();
}
