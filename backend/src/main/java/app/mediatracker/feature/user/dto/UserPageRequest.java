package app.mediatracker.feature.user.dto;
import lombok.Data;
/**
 * Request-Payload zum Anzeigen einer User Page auf Basis einer Username-Search.
 * <p>
 * Das Frontend sendet diesen Typ an den POST-Endpunkt der Bibliothek. Enthält den UserName dessen Page angezeigt werden soll.
 * </p>
 */
@Data
public class UserPageRequest {

    private String username;
    
}
