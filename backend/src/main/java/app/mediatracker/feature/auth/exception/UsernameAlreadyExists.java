package app.mediatracker.feature.auth.exception;

public class UsernameAlreadyExists extends RuntimeException {
    public UsernameAlreadyExists(String message) {
        super(message);
    }

    public UsernameAlreadyExists() {
        super();
    }
}
