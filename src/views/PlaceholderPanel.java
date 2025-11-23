package src.views;

import javax.swing.*;
import java.awt.*;

/**
 * Simple placeholder panel used when a real view
 * has not been implemented yet.
 */
public class PlaceholderPanel extends JPanel {

    public PlaceholderPanel(String message) {
        setLayout(new BorderLayout());

        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 18));

        add(label, BorderLayout.CENTER);
    }
}
