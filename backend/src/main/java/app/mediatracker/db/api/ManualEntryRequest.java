package app.mediatracker.db.api;

import java.util.Map;

import app.mediatracker.db.domain.LibraryEntryStatus;
import lombok.Data;

/**
 * Request-Payload zum Anlegen oder Aktualisieren eines Bibliothekseintrags auf Basis eines Manual Entries.
 * <p>
 * Das Frontend sendet diesen Typ an den POST-Endpunkt der Bibliothek. Enthält den gewünschten Status,
 * eine optionale Bewertung sowie freie Notizen und die vom User ausgefüllten Informationen zum Medien Objekt.
 * </p>
 */
@Data
public class ManualEntryRequest {

    /**
     * Technischer Benutzer-Identifikator. Kann später durch Informationen
     * aus dem authentifizierten Principal ersetzt werden.
     */
    //private String userId;  für später wenn mehrere User

    private LibraryEntryStatus status;

    private Integer rating;

    private String notes;

    private String title;

    private String author;

    private String type;

    //private String genre;

    private String imageUrl;   // Vorschaubild (optional)

    private Map<String, Object> meta;
}