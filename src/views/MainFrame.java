package src.views;

import javax.swing.*;
import java.awt.*;

import src.AppView;
import src.AppActions;
import src.controllers.*;

import src.views.*;
public class MainFrame extends JFrame implements AppView {

    private final CardLayout layout;
    private final JPanel cards;

    public MainFrame(AppActions actions) {
        super("Flight Reservation System");

        this.layout = new CardLayout();
        this.cards = new JPanel(layout);

        cards.add(new LoginPanel(actions), "LOGIN");
        cards.add(new PlaceholderPanel("Customer View (TODO)"), "CUSTOMER");
        cards.add(new PlaceholderPanel("Agent View (TODO)"), "AGENT");
        cards.add(new PlaceholderPanel("Admin View (TODO)"), "ADMIN");

        setContentPane(cards);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    @Override public void showLogin()     { layout.show(cards, "LOGIN"); }
    @Override public void showCustomer()  { layout.show(cards, "CUSTOMER"); }
    @Override public void showAgent()     { layout.show(cards, "AGENT"); }
    @Override public void showAdmin()     { layout.show(cards, "ADMIN"); }

}
