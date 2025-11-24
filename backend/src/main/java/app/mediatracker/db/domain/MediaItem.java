package app.mediatracker.db.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

/**
 * Persistiertes Domain-Objekt für ein Medium (z. B. Anime, Buch, Spiel, Film, Musik).
 * <p>
 * Ein MediaItem repräsentiert die kanonische Speicherung eines extern gefundenen Mediums. Es wird
 * über die Kombination aus {@code type} und {@code externalId} eindeutig identifiziert. Zusätzliche
 * Informationen, die von verschiedenen Such-Providern kommen können, werden in {@code meta}
 * schemalos abgelegt.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "media_items")
public class MediaItem {

    @Id
    private String id;

    /**
     * Logischer Medientyp, z. B. "anime", "music", "movie", ...
     */
    private String type;

    /**
     * Externe Provider-ID (z. B. Jikan-/iTunes-/RAWG-ID).
     * Die Kombination aus (type, externalId) sollte eindeutig sein.
     */
    private String externalId;

    private String title;
    private String imageUrl;
    private String sourceUrl;

    /**
     * Flexible Metainformationen aus externen APIs.
     */
    private Map<String, Object> meta;
}