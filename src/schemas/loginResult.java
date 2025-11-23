package src.schemas;
import src.models.User;

public record loginResult(
    boolean success,
    String message,
    User user
){}
