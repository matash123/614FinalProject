package src;

import src.config.envLoader;
import src.controllers.AppController;
import src.factory.*;

public class MainApp {
    public static void main(String[] args) {
        envLoader.get("DB_PATH");
        AppController app = UIFactory.createApp();
        app.start();
}
}
