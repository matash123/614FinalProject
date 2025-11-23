package src.controllers;

import javax.swing.*;
import src.views.MainFrame;
import src.config.envLoader;


public class AppController {
    private MainFrame frame;

    public void start() {
        frame = new MainFrame();
        frame.setVisible(true);
    }

    public AppController(){
        System.out.println(envLoader.get("DB_PATH"));
    }
}
