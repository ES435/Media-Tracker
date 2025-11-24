package app.mediatracker.library.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Schlanke Darstellung eines MediaItem für API-Responses.
 * <p>
 * Enthält nur die für die UI relevanten Basisinformationen, nicht jedoch die gesamte
 * flexible Metadatenstruktur des persistierten MediaItem.
 * </p>
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