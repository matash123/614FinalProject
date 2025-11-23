package src.views;

import javax.swing.JFrame;
import src.components.*;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.CardLayout;

public class MainFrame extends JFrame {

    private JPanel cards;

    public MainFrame() {
        super("Flight Reservation System");

        cards = new JPanel(new CardLayout());

        cards.add(new LoginPanel(), "LOGIN");

        add(cards);

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void showView(String name) {
        CardLayout cl = (CardLayout) cards.getLayout();
        cl.show(cards, name);
    }
}
