package app.mediatracker.feature.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO-Object for registration requests
 */
@Getter
@Setter
public class RegistrationRequest {
    @NotNull(message = "Username is required")
    private String username;
    @NotNull(message = "Password is required")
    private String password;
    @NotNull(message = "Password confirmation is required")
    private String passwordRep;

    public RegistrationRequest(String username, String password, String passwordRep) {
        this.username = username;
        this.password = password;
        this.passwordRep = passwordRep;
    }
}
