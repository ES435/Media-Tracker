package app.mediatracker.feature.user.dto;
import java.util.List;

import app.mediatracker.feature.library.dto.LibraryEntryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
