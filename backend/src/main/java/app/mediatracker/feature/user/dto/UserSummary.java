package app.mediatracker.feature.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representation of basic user information for user search API responses.
 * <p>
 * This DTO reflects the fields stored as a snapshot in the {@code User} entity.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummary {

    private String username;
    private String profilePictureUrl;
    private Boolean publicList;
}