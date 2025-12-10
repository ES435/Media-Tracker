package app.mediatracker.db.domain.exception;

public class InvalidPasswordException extends RuntimeException{
    public InvalidPasswordException(String username) {
        super("Invalid password for user '" + username + "'");
    }

    //ToDo: s. UserNotFoundException
}
