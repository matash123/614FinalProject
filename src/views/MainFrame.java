package src.views;

import java.awt.*;
import javax.swing.*;
import src.AppFrame;
import src.components.Updatable;
import src.config.Theme;
import src.controllers.AppController;

public class MainFrame extends JFrame implements AppFrame {

    private DynamicPanel current;

    public MainFrame() {
        super("Flight Reservation System");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
    }

    @Override
    public void setView(DynamicPanel p) {
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

    @Override
    public DynamicPanel makeLoginPanel(AppController appController) {
        return new LoginPanel(appController);
    }

    @Override
    public DynamicPanel makeCustomerPanel(){
        return new CustomerPanel();
    }

    @Override
    public DynamicPanel makeAgentPanel() {
        return new AgentPanel();
    }

    @Override
    public DynamicPanel makeAdminPanel() {
        return new AdminPanel();
    }

    @Override
    public void updateCurrentView() {
        if (current instanceof Updatable updatable) {
            updatable.refreshData();
            repaint();
        }
    }
}
