package src.events;

//custom exception types for controllers
public class ControllerExceptions {
    public enum ErrorCode {
        INVALID_CREDENTIALS,
        FLIGHT_NOT_FOUND,
        INSUFFICIENT_SEATS,
        PAYMENT_FAILED,
        RESERVATION_NOT_FOUND,
        UNAUTHORIZED_ACCESS
    }

    public static class ControllerException extends RuntimeException {
        private final ErrorCode code;

        public ControllerException(ErrorCode code, String message) {
            super(message);
            this.code = code;
        }

        public ErrorCode getCode() { return code; }
    }

    //todo add more specific exception types if needed
}

