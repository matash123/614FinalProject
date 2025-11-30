package src.actions;

import src.schemas.loginResult;

/**
 * Actions available on the login screen / session entry point.
 * Views like {@code LoginPanel} depend only on this interface.
 */
public interface LoginActions {

    /**
     * Attempt to authenticate a user with the provided credentials.
     */
    loginResult onLoginAttempt(String username, String password);

    /**
     * Toggle the active UI theme (e.g., light/dark).
     */
    void switchTheme();
}


