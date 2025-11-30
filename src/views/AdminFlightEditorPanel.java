package src.views;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import src.actions.AdminActions;
import src.config.Theme;

/**
 * Placeholder workspace for admins to edit flights themselves
 * (schedule, pricing, capacity, etc.). For now this is a simple
 * themed panel that we can navigate to from the admin dashboard;
 * the detailed editor UI will be added later.
 */
public class AdminFlightEditorPanel extends MainPanel {

    private final AdminActions actions;
    private final JLabel placeholder;

    public AdminFlightEditorPanel(AdminActions actions) {
        this.actions = actions;

        setLayout(new BorderLayout());

        placeholder = new JLabel(
            "Admin flight editor coming soon.",
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


