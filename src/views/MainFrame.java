package src.views;

import java.awt.*;
import javax.swing.*;
import src.AppActions;
import src.AppFrame;
import src.config.Theme;

public class MainFrame extends JFrame implements AppFrame {

    private MainPanel current;

    public MainFrame() {
        super("Flight Reservation System");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
    }

    @Override
    public void setView(MainPanel p) {
        this.current = p;

        getContentPane().removeAll();
        add(p, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    @Override
    public void applyThemeToUI(Theme t) {
        if (this.current != null){
            System.out.println("applying new theme: \n");
            t.printTheme();
             current.refreshTheme(t);
             repaint();
        }
    }

    /* Panel factory methods stay unchanged */
    @Override
    public MainPanel makeLoginPanel(AppActions actions) {
        return new LoginPanel(actions);
    }

    @Override
    public MainPanel makeCustomerPanel(AppActions actions){
        return new CustomerPanel(actions);
    }
}
