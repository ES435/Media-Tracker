package app.mediatracker.feature.library.dto;

import app.mediatracker.feature.library.model.LibraryEntryStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * Request payload to create or update a library entry based on a manual entry.
 * <p>
 * Sent by the frontend to the library POST endpoint. Includes the desired status,
 * an optional rating, free-form notes, and user-provided information about the media item.
 * </p>
 */
@Data
public class ManualEntryRequest {

    /**
     * Technical user identifier.
     * <p>
     * This field is handled via the authenticated Principal (Security Context)
     * and does not need to be provided in the request body.
     * </p>
     */

    private LibraryEntryStatus status;

    private Integer rating;

    private String notes;

    @NotBlank
    private String title;

    private String author;

    @NotBlank
    private String type;

    //private String genre;

    private String imageUrl;   // Preview image (optional)

    private Map<String, Object> meta;
}