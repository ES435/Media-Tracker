package app.mediatracker.feature.library.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.time.Instant;
import java.util.Map;

/**
 * Persisted entry in the user library.
 * <p>
 * Associates a user ({@code userId}) with a stored media item and captures
 * individual information such as status, optional rating, and notes.
 * Additionally, creation and modification timestamps are stored.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndexes({
        // Exactly one entry per user and media item (defined by type + external ID)
        @CompoundIndex(name = "uniq_user_item", def = "{userId: 1, mediaType: 1, externalId: 1}", unique = true),
        @CompoundIndex(name = "idx_user_updatedAt", def = "{userId: 1, updatedAt: -1}")
})
@Document(collection = "user_library_entries")
public class UserLibraryEntry {

    @Id
    private String id;

    /**
     * Technical user identifier (e.g., Subject from a JWT).
     */
    @Indexed(name = "idx_userId")
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId userId;

    // Media snapshot fields (no separate MediaItem required anymore)
    private String mediaType;   // e.g., "anime", "movie", "book", "music"
    private String externalId;  // External ID from the source
    private String title;
    private String imageUrl;
    private String sourceUrl;
    private String author;

    private LibraryEntryStatus status;

    private Map<String, Object> meta;

    /**
     * Optional rating, e.g., on a scale from 1 to 10. May be null.
     */
    private Integer rating;

    /**
     * Optional free-text notes per user and media item.
     */
    private String notes;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}