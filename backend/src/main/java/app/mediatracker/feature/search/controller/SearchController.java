package app.mediatracker.feature.search.controller;

import app.mediatracker.feature.search.core.dto.SearchResult;
import app.mediatracker.feature.search.core.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST controller for search endpoints.
 *
 * Purpose: Accepts HTTP requests from the frontend, prepares parameters,
 * and delegates to the search service.
 *
 * Path: GET /api/search
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Executes a search.
     *
     * Example: /api/search?q=naruto&types=anime,movie&limit=24
     *
     * @param searchQuery search term (e.g., "Naruto")
     * @param types comma-separated list of types (e.g., "anime"); empty or missing = all providers
     * @param limit maximum results per type; default is 24
     * @return combined search results as JSON
     */
    @GetMapping
    public ResponseEntity<List<SearchResult>> search(
            @RequestParam("q") String searchQuery,
            @RequestParam(required = false, defaultValue = "") String types,
            @RequestParam(required = false, defaultValue = "24") int limit) {

        Set<String> typeSet = Arrays.stream(types.split(","))
                .map(String::trim).filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        return ResponseEntity.ok(searchService.search(searchQuery, typeSet, limit));
    }
}