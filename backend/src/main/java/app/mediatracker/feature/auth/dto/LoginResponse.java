package app.mediatracker.feature.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
@Builder
public class LoginResponse {
    private String username;
    private String profilePictureUrl;
    private boolean publicList;
}
