package src.components;

import java.awt.*;
import javax.swing.*;
import src.config.Theme;
import src.controllers.AppController;

public class UserBox extends JPanel implements ThemeAware {

    private JLabel nameLabel, emailLabel, roleLabel;
    private JButton logoutButton;

    public UserBox() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setOpaque(true);

        nameLabel  = new JLabel("Welcome, User");
        emailLabel = new JLabel("email@example.com");
        roleLabel  = new JLabel("Role: CUSTOMER");
        logoutButton = new JButton("Logout");

        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        logoutButton.setAlignmentX(LEFT_ALIGNMENT);
        logoutButton.addActionListener(e -> {
            AppController app = AppController.getInstance();
            if (app != null) {
                app.logout();
            }
        });

        add(nameLabel);
        add(emailLabel);
        add(roleLabel);

        add(Box.createVerticalStrut(8));
        add(logoutButton);
    }

    public void setUser(String name, String email, String role) {
        nameLabel.setText("Welcome, " + name);
        emailLabel.setText(email);
        roleLabel.setText("Role: " + role);
        revalidate();
        repaint();
    }

    @Override
    public void refreshTheme(Theme t) {
        setBackground(t.bg);
        nameLabel.setForeground(t.fg);
        emailLabel.setForeground(t.fg);
        roleLabel.setForeground(t.fg);

        if (logoutButton != null) {
            logoutButton.setBackground(t.buttonBg);
            logoutButton.setForeground(t.buttonFg);
        }
    }
}
