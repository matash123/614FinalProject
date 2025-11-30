/**
 * 
 * Okay this was the very last view we created that entailed the actual signing up rpocess
 * We have so much fleshed out that it was pretty easy to rip thrugg
 * Full Reference to ChatGPT for all the help in structure and resuing old cold effectively/debugging
 * 
 * This page came from a lot of our LOGIN PANEL page, as that was done first meticiously this was STRAIGHT forward (yaya!
 * 
 */

package src.views;

import java.awt.*;
import javax.swing.*;

import src.config.Theme;
import src.controllers.AppController;
import src.controllers.UserController;
import src.factory.ControllerFactory;
import src.schemas.loginResult;

public class SignupPanel extends DynamicPanel {

    private final AppController appController;
    private final UserController userController;

    private JLabel titleLabel, userLabel, passLabel, confirmLabel, errorLabel;
    private JTextField userField;
    private JPasswordField passField, confirmField;
    private JButton submitButton, backButton;

    public SignupPanel(AppController appController) {
        this.appController = appController;
        this.userController = ControllerFactory.getInstance().user();

        buildUI();
    }

    private void buildUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 12, 10, 12);
        c.fill = GridBagConstraints.HORIZONTAL;

        titleLabel   = new JLabel("Create Account", SwingConstants.CENTER);
        userLabel    = new JLabel("Username:");
        passLabel    = new JLabel("Password:");
        confirmLabel = new JLabel("Confirm Password:");
        errorLabel   = new JLabel("", SwingConstants.CENTER);

        userField    = new JTextField();
        passField    = new JPasswordField();
        confirmField = new JPasswordField();

        submitButton = new JButton("Submit");
        submitButton.setFocusPainted(false);
        submitButton.setContentAreaFilled(false);
        submitButton.setOpaque(true);
        submitButton.setBorderPainted(false);

        backButton = new JButton("Back to Login");
        backButton.setFocusPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setOpaque(true);
        backButton.setBorderPainted(false);

        // The layout of our Page
        c.gridy = 0; c.gridx = 0; c.gridwidth = 2; add(titleLabel, c);

        c.gridy = 1; c.gridwidth = 1; c.gridx = 0; add(userLabel, c);
        c.gridx = 1; add(userField, c);

        c.gridy = 2; c.gridx = 0; add(passLabel, c);
        c.gridx = 1; add(passField, c);

        c.gridy = 3; c.gridx = 0; add(confirmLabel, c);
        c.gridx = 1; add(confirmField, c);

        c.gridy = 4; c.gridx = 0; c.gridwidth = 2; add(errorLabel, c);

        c.gridy = 5; c.gridwidth = 1; c.gridx = 0; add(submitButton, c);
        c.gridx = 1; add(backButton, c);

        //Actual behaviour
        submitButton.addActionListener(e -> handleSubmit());
        backButton.addActionListener(e -> appController.showLogin());
    }

    private void handleSubmit() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword());
        String confirm  = new String(confirmField.getPassword());

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }

        if (!password.equals(confirm)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        // Call UserController → userCRUD to create user
        loginResult result = userController.registerCustomer(username, password);

        if (result.success()) {
            errorLabel.setText("");
            // AUTO-LOGIN: same as login panel does
            appController.onLoginSuccess(result.user());
        } else {
            // Show error from userCRUD/UserController on this page
            errorLabel.setText(result.message());
        }
    }

    @Override
    public void refreshTheme(Theme t) {
        setBackground(t.bg);

        titleLabel.setForeground(t.fg);
        userLabel.setForeground(t.fg);
        passLabel.setForeground(t.fg);
        confirmLabel.setForeground(t.fg);
        errorLabel.setForeground(t.errorFg);

        userField.setBackground(t.inputBg);
        userField.setForeground(t.inputFg);
        passField.setBackground(t.inputBg);
        passField.setForeground(t.inputFg);
        confirmField.setBackground(t.inputBg);
        confirmField.setForeground(t.inputFg);

        submitButton.setBackground(t.buttonBg);
        submitButton.setForeground(t.buttonFg);
        backButton.setBackground(t.buttonBg);
        backButton.setForeground(t.buttonFg);

        repaint();
    }
}
