package src;

import src.controllers.AppController;
import src.factory.*;

public class MainApp {
    public static void main(String[] args) {
    AppController app = UIFactory.createApp();
    app.start();
}
}
