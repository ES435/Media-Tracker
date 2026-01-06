package app.mediatracker.db.api;
import java.util.List;

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

    private List<LibraryEntryResponse> mediaList;

}
