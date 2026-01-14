package app.mediatracker.db.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO Object for login requests
 */
@Setter
@Getter
public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
