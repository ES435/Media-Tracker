package app.mediatracker.exception;

import app.mediatracker.feature.auth.exception.InvalidPasswordException;
import app.mediatracker.feature.auth.exception.PasswordsDontMatchException;
import app.mediatracker.feature.auth.exception.UsernameAlreadyExistsException;
import app.mediatracker.feature.user.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UserNotFoundException.class, InvalidPasswordException.class})
    public ResponseEntity<Object> handleAuthenticationExceptions(Exception exception) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Login failed. Invalid username or password.");
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Object> handleUsernameAlreadyExists(UsernameAlreadyExistsException exception) {
        return buildResponse(HttpStatus.CONFLICT, "Username already exists.");
    }

    @ExceptionHandler(PasswordsDontMatchException.class)
    public ResponseEntity<Object> handlePasswordsDontMatch(PasswordsDontMatchException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Passwords don't match.");
    }

    private ResponseEntity<Object> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        return new ResponseEntity<>(body, status);
    }

}
