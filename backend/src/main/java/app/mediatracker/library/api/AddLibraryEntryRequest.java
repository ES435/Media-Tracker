package app.mediatracker.library.api;

import app.mediatracker.core.dto.SearchResult;
import app.mediatracker.library.domain.LibraryEntryStatus;
import lombok.Data;

/**
 * Request-Payload zum Anlegen oder Aktualisieren eines Bibliothekseintrags auf Basis eines SearchResult.
 * <p>
 * Das Frontend sendet diesen Typ an den POST-Endpunkt der Bibliothek. Enthält den gewünschten Status,
 * eine optionale Bewertung sowie freie Notizen und das ausgewählte Suchergebnis.
 * </p>
 */
@Data
public class AddLibraryEntryRequest {

    /**
     * Technischer Benutzer-Identifikator. Kann später durch Informationen
     * aus dem authentifizierten Principal ersetzt werden.
     */
    //private String userId;  für später wenn mehrere User

    private LibraryEntryStatus status;

    private Integer rating;

    private String notes;

    /**
     * Das ausgewählte Suchergebnis, das in der Bibliothek gespeichert werden soll.
     */
    private SearchResult searchResult;
}