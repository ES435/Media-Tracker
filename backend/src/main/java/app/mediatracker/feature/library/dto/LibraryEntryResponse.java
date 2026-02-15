package app.mediatracker.feature.library.dto;

import app.mediatracker.feature.library.model.LibraryEntryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * API response for a single library entry including the associated media snapshot.
 * <p>
 * This DTO is optimized for frontend rendering. It combines the user-specific entry data and
 * a compact view of the linked media ({@link MediaItemSummary}).
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryEntryResponse {

    private String id;
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId userId;
    private LibraryEntryStatus status;
    private Integer rating;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;

    private MediaItemSummary mediaItem;
}