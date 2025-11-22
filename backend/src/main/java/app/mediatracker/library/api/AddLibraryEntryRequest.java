package app.mediatracker.library.api;

import app.mediatracker.core.dto.SearchResult;
import app.mediatracker.library.domain.LibraryEntryStatus;
import lombok.Data;

/**
 * Request payload to add or update a library entry based on a SearchResult.
 */
@Data
public class AddLibraryEntryRequest {

    /**
     * Technical user identifier. Later you can replace this
     * with information from the authenticated principal.
     */
    //private String userId;  für später wenn mehrere User

    private LibraryEntryStatus status;

    private Integer rating;

    private String notes;

    /**
     * The selected search result that should be stored in the library.
     */
    private SearchResult searchResult;
}