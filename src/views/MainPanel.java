package src.views;

import javax.swing.*;
import src.config.Theme;

public abstract class MainPanel extends JPanel {
    public abstract void refreshTheme(Theme t);
}
