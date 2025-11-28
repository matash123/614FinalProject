package src;


import src.config.Theme;
import src.views.MainPanel;

public interface AppFrame {

    void setView(MainPanel p);
    
    MainPanel makeLoginPanel(AppActions a);
    MainPanel makeCustomerPanel(AppActions a);
    
    void applyThemeToUI(Theme t);
    
} 
