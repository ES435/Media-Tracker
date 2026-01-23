package app.mediatracker.feature.auth.dto;

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
    private Boolean rememberMe;

    public LoginRequest(String username, String password, Boolean rememberMe) {
        this.username = username;
        this.password = password;
        this.rememberMe = rememberMe;
    }
}
