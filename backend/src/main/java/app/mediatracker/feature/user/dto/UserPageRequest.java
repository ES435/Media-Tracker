package app.mediatracker.feature.user.dto;

import lombok.Data;

/**
 * Request payload for displaying a user page based on a username search.
 * <p>
 * The frontend sends this type to the library POST endpoint. Contains the username
 * of the user whose page should be displayed.
 * </p>
 */
@Data
public class UserPageRequest {

    private String username;

}