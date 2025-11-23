package src.factory;

import src.controllers.AppController;
import src.views.MainFrame;
import src.AppActions;
import src.AppView;

public class UIFactory {

    public static AppController createApp() {
        AppController controller = new AppController();

        AppActions actions = controller;     // controller handles UI callbacks
        AppView view = new MainFrame(actions);  // main window implements AppView

        controller.setView(view);

        if (view instanceof java.awt.Component c) {
            c.setVisible(true);
        }

        return controller;
    }
}
