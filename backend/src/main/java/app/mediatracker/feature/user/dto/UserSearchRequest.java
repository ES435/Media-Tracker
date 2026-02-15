package app.mediatracker.feature.user.dto;

import lombok.Data;

/**
 * Request payload for searching for existing users based on a name or a part thereof (namePart).
 * <p>
 * The frontend sends this type to the endpoint. Contains the partial username to search for.
 * </p>
 */
@Data
public class UserSearchRequest {

    private String namePart;

}