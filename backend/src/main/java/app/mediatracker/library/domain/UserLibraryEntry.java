package app.mediatracker.library.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_library_entries")
public class UserLibraryEntry {

    @Id
    private String id;

    /**
     * Technical user identifier (e.g. subject from JWT).
     */
    private String userId;

    /**
     * Reference to the stored MediaItem.
     */
    private String mediaItemId;

    private LibraryEntryStatus status;

    /**
     * Optional rating, e.g. 1-10 scale. May be null.
     */
    private Integer rating;

    /**
     * Optional free-form notes per user and media item.
     */
    private String notes;

    private Instant createdAt;
    private Instant updatedAt;
}