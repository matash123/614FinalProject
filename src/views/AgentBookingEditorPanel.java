package src.views;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import src.config.Theme;

/**
 * Placeholder workspace for agents to edit bookings / reservations.
 * For now this is a simple themed panel that we can navigate to from
 * the main agent dashboard; the detailed editor UI will be added later.
 */
public class AgentBookingEditorPanel extends MainPanel {

    private final JLabel placeholder;

    public AgentBookingEditorPanel() {
        setLayout(new BorderLayout());

        placeholder = new JLabel(
            "Agent booking / reservation editor coming soon.",
            SwingConstants.CENTER
        );
        placeholder.setFont(new Font("SansSerif", Font.PLAIN, 18));

        add(placeholder, BorderLayout.CENTER);
    }

    @Override
    public void refreshTheme(Theme t) {
        setBackground(t.bg);
        placeholder.setForeground(t.fg);
        repaint();
    }
}


