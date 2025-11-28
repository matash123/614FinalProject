package src;


import src.config.Theme;
import src.views.MainPanel;

public interface AppFrame {
    void showLogin(AppActions a);
    void setView(MainPanel p);
    MainPanel makeLoginPanel(AppActions a);
    void applyThemeToUI(Theme t);
} 
