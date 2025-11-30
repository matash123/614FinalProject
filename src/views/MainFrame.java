package src.views;

import java.awt.*;
import javax.swing.*;
import src.AppFrame;
import src.actions.CustomerActions;
import src.actions.LoginActions;
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

    /* Panel factory methods stay unchanged in behavior, but now use role-specific actions */
    @Override
    public MainPanel makeLoginPanel(LoginActions actions) {
        return new LoginPanel(actions);
    }

    @Override
    public MainPanel makeCustomerPanel(CustomerActions actions){
        return new CustomerPanel(actions);
    }
}
