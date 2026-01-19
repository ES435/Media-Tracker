package app.mediatracker.feature.user.dto;

import app.mediatracker.feature.library.dto.LibraryEntryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * API-Response für eine einzelne User-Page inklusive zugehöriger MediaItem-Daten 
 * (falls die Liste auf Public gestellt ist).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPageResponse {

    private UserSummary user;

    private List<LibraryEntryResponse> userMediaList;

}
