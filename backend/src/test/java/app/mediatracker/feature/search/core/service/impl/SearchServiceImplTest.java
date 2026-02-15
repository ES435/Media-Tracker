package app.mediatracker.feature.search.core.service.impl;

import app.mediatracker.feature.search.core.dto.SearchResult;
import app.mediatracker.feature.search.core.provider.SearchProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * Unit test for the SearchServiceImpl.
 * Verifies the aggregation logic and the type-filtering capabilities of the central search service.
 */
@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {

    @Mock
    private SearchProvider animeProvider;

    @Mock
    private SearchProvider movieProvider;

    private SearchServiceImpl searchService;

    @BeforeEach
    void setUp() {
        // Use lenient() because not every test case triggers interactions with all providers.
        // This prevents UnnecessaryStubbingExceptions when specific types are filtered.
        lenient().when(animeProvider.getType()).thenReturn("anime");
        lenient().when(movieProvider.getType()).thenReturn("movie");

        // Inject the mocked providers into the service implementation
        searchService = new SearchServiceImpl(List.of(animeProvider, movieProvider));
    }

    /**
     * Verifies that the service correctly calls all available providers and
     * aggregates their results into a single list.
     */
    @Test
    void search_shouldAggregateResultsFromMultipleProviders() {
        // Arrange
        SearchResult animeResult = SearchResult.builder().type("anime").id("1").title("Naruto").build();
        SearchResult movieResult = SearchResult.builder().type("movie").id("2").title("Inception").build();

        when(animeProvider.search("term", 10)).thenReturn(List.of(animeResult));
        when(movieProvider.search("term", 10)).thenReturn(List.of(movieResult));

        // Act
        List<SearchResult> results = searchService.search("term", null, 10);

        // Assert
        assertEquals(2, results.size(), "Should aggregate results from both providers");
    }

    /**
     * Verifies that the service only invokes providers that match the requested types.
     */
    @Test
    void search_shouldFilterTypes_whenSpecified() {
        // Arrange
        SearchResult animeResult = SearchResult.builder().type("anime").id("1").title("Naruto").build();
        when(animeProvider.search("term", 10)).thenReturn(List.of(animeResult));

        // Act: Search specifically for "anime" only
        List<SearchResult> results = searchService.search("term", Set.of("anime"), 10);

        // Assert
        assertEquals(1, results.size());
        assertEquals("anime", results.get(0).getType());
        // Note: MovieProvider is implicitly ignored because its type wasn't in the filter set.
    }
}