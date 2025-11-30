package src;

import src.actions.AdminActions;
import src.actions.AgentActions;
import src.actions.CustomerActions;
import src.actions.LoginActions;
import src.config.Theme;
import src.views.MainPanel;

public interface AppFrame {

    void setView(MainPanel p);

    MainPanel makeLoginPanel(LoginActions a);
    MainPanel makeCustomerPanel(CustomerActions a);
    MainPanel makeAgentPanel(AgentActions a);
    MainPanel makeAdminPanel(AdminActions a);

    void applyThemeToUI(Theme t);

}
