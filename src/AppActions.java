package src;

import src.schemas.loginResult;
import src.models.User;

public interface AppActions {
    loginResult onLoginAttempt(String username, String password);
    void onLoginSuccess(User user);
}
