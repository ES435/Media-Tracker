package app.mediatracker.feature.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Darstellung der User-Basisinformationen für API-Responses der User-Search.
 * <p>
 * Dieses DTO spiegelt die Felder wider, die als Snapshot in {@code User}
 * gespeichert werden.
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
