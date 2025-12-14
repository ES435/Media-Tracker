package app.mediatracker.db.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO-Object for registration requests
 */
@Getter
@Setter
public class RegistrationRequest {
    private String username;
    private String password;

    public RegistrationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
