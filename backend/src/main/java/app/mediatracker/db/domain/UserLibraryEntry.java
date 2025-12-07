package app.mediatracker.db.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Map;

import java.time.Instant;

/**
 * Persistierter Eintrag in der Benutzerbibliothek.
 * <p>
 * Verknüpft einen User ({@code userId}) mit einem gespeicherten Medium und
 * hält individuelle Informationen wie Status, optionale Bewertung und Notizen fest. Zusätzlich werden
 * Erstell- und Änderungszeitpunkt gespeichert.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndexes({
        // Pro Nutzer und Medium (definiert über Typ + externe ID) genau ein Eintrag
        @CompoundIndex(name = "uniq_user_item", def = "{userId: 1, mediaType: 1, externalId: 1}", unique = true),
        @CompoundIndex(name = "idx_user_updatedAt", def = "{userId: 1, updatedAt: -1}")
})
@Document(collection = "user_library_entries")
public class UserLibraryEntry {

    @Id
    private String id;

    /**
     * Technischer Benutzer-Identifikator (z. B. Subject aus einem JWT).
     */
    @Indexed(name = "idx_userId")
    private String userId;

    // Medien-Snapshot Felder (kein separates MediaItem mehr nötig)
    private String mediaType;   // z. B. "anime", "movie", "book", "music"
    private String externalId;  // externe ID aus der Quelle
    private String title;
    private String imageUrl;
    private String sourceUrl;

    private LibraryEntryStatus status;

    private Map<String, Object> meta;

    /**
     * Optionale Bewertung, z. B. auf einer Skala von 1 bis 10. Darf null sein.
     */
    private Integer rating;

    /**
     * Optionale Freitextnotizen pro Benutzer und Medium.
     */
    private String notes;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}