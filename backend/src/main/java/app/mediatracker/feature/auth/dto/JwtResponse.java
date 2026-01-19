package app.mediatracker.feature.auth.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO Object for jwt requests
 */
@Setter
@Getter
public class JwtResponse {
    private String token;

    public JwtResponse(String token) {
        this.token = token;
    }

}
