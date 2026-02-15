package app.mediatracker.feature.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Compact representation of basic media information for API responses.
 * <p>
 * This DTO mirrors the fields stored as a snapshot in {@code UserLibraryEntry}
 * (no separate, persisted MediaItem). It contains only the UI-relevant basics.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaItemSummary {

    private String type;
    private String externalId;
    private String title;
    private String imageUrl;
    private String sourceUrl;
}