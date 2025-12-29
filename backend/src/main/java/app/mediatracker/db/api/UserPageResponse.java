package app.mediatracker.db.api;
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

    private String username;

    private String profilePictureUrl;

    private Boolean publicList;

    //private List<MediaEntry> entries;
    
}
