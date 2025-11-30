package src;

import src.config.Theme;
import src.controllers.AppController;
import src.views.MainPanel;

public interface AppFrame {

    void setView(MainPanel p);

    MainPanel makeLoginPanel(AppController appController);
    MainPanel makeCustomerPanel();
    MainPanel makeAgentPanel();
    MainPanel makeAdminPanel();

    void applyThemeToUI(Theme t);

}
