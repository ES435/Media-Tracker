package app.mediatracker.feature.search.core.provider;

import app.mediatracker.feature.search.core.dto.SearchResult;

import java.util.List;

/**
 * Base interface for search providers of a specific media type.
 * Purpose: Each provider is responsible for exactly one type (e.g., "anime")
 * and knows how to query an external source and translate results
 * into our internal {@link SearchResult} format.
 * Extensibility: To support a new media type, create a new implementation of this
 * interface, annotate it as a Spring bean (e.g., with {@code @Component}),
 * and return the type name from {@link #getType()}.
 */
public interface SearchProvider {
    /**
     * Unique media type this provider serves (e.g., "anime", "movie").
     */
    String getType();

    /**
     * Executes the search against the respective external service and returns normalized results.
     *
     * @param searchQuery search term
     * @param limit maximum number of results
     * @return list of search results
     */
    List<SearchResult> search(String searchQuery, int limit);
}