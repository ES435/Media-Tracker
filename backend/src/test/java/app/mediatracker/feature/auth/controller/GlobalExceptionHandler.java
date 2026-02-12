package app.mediatracker.feature.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        // Gibt HTTP 400 mit der Fehlermeldung zurück
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
