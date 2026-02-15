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

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {

    @Mock SearchProvider animeProvider;
    @Mock SearchProvider movieProvider;

    private SearchServiceImpl searchService;

    @BeforeEach
    void setUp() {
        // Use lenient() because not every test case uses both providers
        // (e.g. "search_shouldFilterTypes_whenSpecified" only uses animeProvider)
        lenient().when(animeProvider.getType()).thenReturn("anime");
        lenient().when(movieProvider.getType()).thenReturn("movie");

        // Create service with the list of mocks
        searchService = new SearchServiceImpl(List.of(animeProvider, movieProvider));
    }

    @Test
    void search_shouldAggregateResultsFromMultipleProviders() {
        SearchResult animeResult = SearchResult.builder().type("anime").id("1").title("Naruto").build();
        SearchResult movieResult = SearchResult.builder().type("movie").id("2").title("Inception").build();

        when(animeProvider.search("term", 10)).thenReturn(List.of(animeResult));
        when(movieProvider.search("term", 10)).thenReturn(List.of(movieResult));

        List<SearchResult> results = searchService.search("term", null, 10);

        assertEquals(2, results.size());
    }

    @Test
    void search_shouldFilterTypes_whenSpecified() {
        SearchResult animeResult = SearchResult.builder().type("anime").id("1").title("Naruto").build();

        when(animeProvider.search("term", 10)).thenReturn(List.of(animeResult));

        // We search only for "anime", so MovieProvider should not be used
        List<SearchResult> results = searchService.search("term", Set.of("anime"), 10);

        assertEquals(1, results.size());
        assertEquals("anime", results.get(0).getType());
    }
}