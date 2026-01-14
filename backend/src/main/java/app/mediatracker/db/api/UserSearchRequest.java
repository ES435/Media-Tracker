package app.mediatracker.db.api;
import lombok.Data;
/**
 * Request-Payload zum Suchen nach existierenden Usern, auf Basis eines Namens oder Teils davon (namePart).
 * <p>
 * Das Frontend sendet diesen Typ an den POST-Endpunkt der Bibliothek. Enthält den partiellen UserName der gesucht werden soll.
 * </p>
 */
@Data
public class UserSearchRequest {

    private String namePart;
    
}
