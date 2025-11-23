package src.views;

import javax.swing.*;
import java.awt.*;

import src.AppActions;
import src.schemas.loginResult;

public class LoginPanel extends JPanel {

    private JLabel errorLabel;
    private JTextField userField;
    private JPasswordField passField;

    public LoginPanel(AppActions actions) {

        setLayout(new GridLayout(4, 1));

        userField = new JTextField();
        passField = new JPasswordField();

        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setForeground(Color.RED);

        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            login(username, password, actions);
        });

        add(new JLabel("Username:"));
        add(userField);
        add(new JLabel("Password:"));
        add(passField);
        add(errorLabel);
        add(loginButton);
    }

    private void login(String username, String password, AppActions actions){
        loginResult result = actions.onLoginAttempt(username, password);

        if (!result.success()) {
            errorLabel.setText(result.message());
            return;     // stay here
        }

        // SUCCESS
        actions.onLoginSuccess(result.user());
    }
}
