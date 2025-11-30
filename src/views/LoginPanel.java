package src.views;

import java.awt.*;
import javax.swing.*;
import src.config.Theme;
import src.controllers.AppController;
import src.controllers.UserController;
import src.factory.ControllerFactory;
import src.schemas.loginResult;


public class LoginPanel extends MainPanel {

    private JLabel title, userLabel, passLabel, errorLabel;
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;
    private JButton themeButton;

    private final AppController appController;
    private final UserController userController;

    public LoginPanel(AppController appController) {
        this.appController = appController;
        this.userController = ControllerFactory.getInstance().user();

        // build UI normally
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 12, 10, 12);
        c.fill = GridBagConstraints.HORIZONTAL;

        title = new JLabel("Flight Reservation Login", SwingConstants.CENTER);
        userLabel = new JLabel("Username:");
        passLabel = new JLabel("Password:");
        errorLabel = new JLabel("", SwingConstants.CENTER);

        userField = new JTextField();
        passField = new JPasswordField();
        loginButton = new JButton("Login");
        loginButton.setFocusPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);

        loginButton.addActionListener(e -> {
            var u = userField.getText();
            var p = new String(passField.getPassword());

            loginResult result = userController.attemptLogin(u, p);
            if (result.success()) {
                errorLabel.setText("");
                appController.onLoginSuccess(result.user());
            } else {
                errorLabel.setText(result.message());
            }
        });

        themeButton = new JButton("Switch Theme");
        themeButton.setFocusPainted(false);
        themeButton.setContentAreaFilled(false);
        themeButton.setOpaque(true);
        themeButton.setBorderPainted(false);

        themeButton.addActionListener(e -> appController.switchTheme());

        c.gridy = 0; c.gridwidth = 2; add(title, c);
        c.gridy = 1; c.gridwidth = 1; c.gridx = 0; add(userLabel, c);
        c.gridx = 1; add(userField, c);
        c.gridx = 0; c.gridy = 2; add(passLabel, c);
        c.gridx = 1; add(passField, c);
        c.gridx = 0; c.gridy = 3; c.gridwidth = 2; add(errorLabel, c);

        c.gridy = 4; add(loginButton, c);
        c.gridy = 5; add(themeButton, c);
    }

    @Override
    public void refreshTheme(Theme t) {
        setBackground(t.bg);

        title.setForeground(t.fg);
        userLabel.setForeground(t.fg);
        passLabel.setForeground(t.fg);
        errorLabel.setForeground(t.errorFg);

        userField.setBackground(t.inputBg);
        userField.setForeground(t.inputFg);
        passField.setBackground(t.inputBg);
        passField.setForeground(t.inputFg);

        loginButton.setBackground(t.buttonBg);
        loginButton.setForeground(t.buttonFg);

        themeButton.setBackground(t.buttonBg);
        themeButton.setForeground(t.buttonFg);

        repaint();
    }
}
