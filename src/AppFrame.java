package src;

import src.actions.CustomerActions;
import src.actions.LoginActions;
import src.config.Theme;
import src.views.MainPanel;

public interface AppFrame {

    void setView(MainPanel p);

    MainPanel makeLoginPanel(LoginActions a);
    MainPanel makeCustomerPanel(CustomerActions a);

    void applyThemeToUI(Theme t);

}
