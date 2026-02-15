package app.mediatracker.feature.library.dto;

import app.mediatracker.feature.library.model.LibraryEntryStatus;
import app.mediatracker.feature.search.core.dto.SearchResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request payload to create or update a library entry based on a SearchResult.
 * <p>
 * Sent by the frontend to the library POST endpoint. Includes the desired status,
 * an optional rating, free-form notes, and the selected search result.
 * </p>
 */
@Data
public class AddLibraryEntryRequest {

    /**
     * Technical user identifier.
     * <p>
     * This field is handled via the authenticated Principal (Security Context)
     * and does not need to be provided in the request body.
     * </p>
     */

    @NotNull
    private LibraryEntryStatus status;

    @Min(1)
    @Max(10)
    private Integer rating;

    @Size(max = 2000)
    private String notes;

    /**
     * The selected search result to be saved in the library.
     */
    @NotNull
    @Valid
    private SearchResult searchResult;
}