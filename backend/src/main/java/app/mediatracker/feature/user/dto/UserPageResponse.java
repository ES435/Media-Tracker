package app.mediatracker.feature.user.dto;

import app.mediatracker.feature.library.dto.LibraryEntryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * API response for a single user page including associated MediaItem data
 * (if the list is set to public).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPageResponse {

    private UserSummary user;

    private List<LibraryEntryResponse> userMediaList;

}
