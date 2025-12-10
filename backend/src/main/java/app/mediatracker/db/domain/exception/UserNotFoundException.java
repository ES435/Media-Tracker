package app.mediatracker.db.domain.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super("User with username '" + username + "' not found");
    }

    // ToDO: Glaube hier musste noch iwas rein -> Folien schauen
}
