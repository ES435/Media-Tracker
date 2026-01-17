package app.mediatracker.feature.auth.exception;

public class InvalidPasswordException extends RuntimeException{
    public InvalidPasswordException(String message) {
        super(message);
    }
    public InvalidPasswordException() {
        super();
    }
}
