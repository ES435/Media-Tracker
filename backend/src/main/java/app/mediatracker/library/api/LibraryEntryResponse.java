package app.mediatracker.library.api;

import app.mediatracker.library.domain.LibraryEntryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * API response for a single library entry including media item data.
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