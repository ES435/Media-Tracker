package app.mediatracker.library.api;

import app.mediatracker.library.domain.LibraryEntryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * API-Response für einen einzelnen Bibliothekseintrag inklusive zugehöriger MediaItem-Daten.
 * <p>
 * Dieses DTO ist für die Darstellung im Frontend optimiert. Es fasst die Daten des User-Eintrags und
 * eine schlanke Ansicht des verknüpften Mediums ({@link MediaItemSummary}) zusammen.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryEntryResponse {

    private String id;
    private String userId;
    private LibraryEntryStatus status;
    private Integer rating;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;

    private MediaItemSummary mediaItem;
}