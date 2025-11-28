package src;

import src.schemas.loginResult;

public interface AppActions {
    loginResult onLoginAttempt(String username, String password);
    void switchTheme();
}
