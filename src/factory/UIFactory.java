package src.factory;

import src.AppFrame;
import src.config.Theme;
import src.config.envLoader;
import src.controllers.AppController;
import src.views.MainFrame;


public class UIFactory {

    public static AppController createApp() {
        
        String envTheme = envLoader.get("DEFAULT_THEME");
        Theme defaultTheme = Theme.LIGHT;

        if(envTheme.equals("DARK")){
            defaultTheme = Theme.DARK;
        }


        System.out.println("Building App with default theme as " + envTheme);

        AppController controller = new AppController(defaultTheme);
        AppFrame view = new MainFrame(); 

        controller.setMainFrame(view);

        if (view instanceof java.awt.Component c) {
            c.setVisible(true);
        }

        return controller;
    }
}
