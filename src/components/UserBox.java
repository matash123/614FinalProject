package src.components;

import javax.swing.*;
import java.awt.*;
import src.config.Theme;

public class UserBox extends JPanel implements ThemeAware {

    private JLabel nameLabel, emailLabel, roleLabel;

    public UserBox() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setOpaque(true);

        nameLabel  = new JLabel("Welcome, User");
        emailLabel = new JLabel("email@example.com");
        roleLabel  = new JLabel("Role: CUSTOMER");

        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        add(nameLabel);
        add(emailLabel);
        add(roleLabel);
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
    }
}
