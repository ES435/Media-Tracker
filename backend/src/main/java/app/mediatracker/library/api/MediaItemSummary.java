package app.mediatracker.library.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight view of a MediaItem for API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaItemSummary {

    private String id;
    private String type;
    private String externalId;
    private String title;
    private String imageUrl;
    private String sourceUrl;
}