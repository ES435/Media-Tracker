package app.mediatracker.db.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Persistierter Eintrag in der Benutzerbibliothek.
 * <p>
 * Verknüpft einen User ({@code userId}) mit einem gespeicherten Medium ({@code mediaItemId}) und
 * hält individuelle Informationen wie Status, optionale Bewertung und Notizen fest. Zusätzlich werden
 * Erstell- und Änderungszeitpunkt gespeichert.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_library_entries")
public class UserLibraryEntry {

    @Id
    private String id;

    /**
     * Technischer Benutzer-Identifikator (z. B. Subject aus einem JWT).
     */
    private String userId;

    /**
     * Referenz auf das gespeicherte MediaItem.
     */
    private String mediaItemId;

    private LibraryEntryStatus status;

    /**
     * Optionale Bewertung, z. B. auf einer Skala von 1 bis 10. Darf null sein.
     */
    private Integer rating;

    /**
     * Optionale Freitextnotizen pro Benutzer und Medium.
     */
    private String notes;

    private Instant createdAt;
    private Instant updatedAt;
}