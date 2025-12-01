package app.mediatracker.db.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Schlanke Darstellung der Medien-Basisinformationen für API-Responses.
 * <p>
 * Dieses DTO spiegelt die Felder wider, die als Snapshot in {@code UserLibraryEntry}
 * gespeichert werden (kein separates, persistiertes MediaItem). Es enthält nur die für die
 * UI relevanten Basisinformationen.
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