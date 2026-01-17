package app.mediatracker.feature.library.dto;

import app.mediatracker.search.core.dto.SearchResult;
import app.mediatracker.feature.library.model.LibraryEntryStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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

    @NotNull
    private LibraryEntryStatus status;

    @Min(1)
    @Max(10)
    private Integer rating;

    @Size(max = 2000)
    private String notes;

    /**
     * Das ausgewählte Suchergebnis, das in der Bibliothek gespeichert werden soll.
     */
    @NotNull
    @Valid
    private SearchResult searchResult;
}