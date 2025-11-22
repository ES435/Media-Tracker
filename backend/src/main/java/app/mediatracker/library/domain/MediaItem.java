package app.mediatracker.library.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "media_items")
public class MediaItem {

    @Id
    private String id;

    /**
     * Logical type of the media, e.g. "anime", "music", "movie", ...
     */
    private String type;

    /**
     * External provider id (e.g. Jikan / iTunes / RAWG id).
     * Combination of (type, externalId) should be unique.
     */
    private String externalId;

    private String title;
    private String imageUrl;
    private String sourceUrl;

    /**
     * Flexible meta information coming from external APIs.
     */
    private Map<String, Object> meta;
}