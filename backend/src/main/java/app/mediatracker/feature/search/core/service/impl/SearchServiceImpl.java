package app.mediatracker.feature.search.core.service.impl;

import app.mediatracker.feature.search.core.dto.SearchResult;
import app.mediatracker.feature.search.core.provider.SearchProvider;
import app.mediatracker.feature.search.core.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the search service.
 * Purpose: Queries all registered {@link SearchProvider}s, optionally filters by types,
 * and merges the results into a single list. Duplicate entries (same combination of
 * {@code type} and {@code id}) are removed.
 */
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    /**
     * List of all available providers injected by Spring (one bean per media type).
     */
    private final List<SearchProvider> providers;

    /**
     * Searches across all matching providers and combines results.
     * Behavior:
     * - When {@code types} is empty or {@code null}, all providers are used.
     * - Each provider is limited by {@code limitPerType}.
     * - Duplicates are removed based on the {@code type#id} key (stable insertion order).
     */
    @Override
    public List<SearchResult> search(String searchQuery, Set<String> types, int limitPerType) {
        // Aggregation + Duplikatbereinigung nach Schlüssel (type#id)
        Map<String, SearchResult> map = providers.stream()
                .filter(provider -> types == null || types.isEmpty() || types.contains(provider.getType()))
                .flatMap(p -> p.search(searchQuery, limitPerType).stream())
                .collect(Collectors.toMap(
                        result -> result.getType() + "#" + result.getId(),
                        result -> result,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
        return new ArrayList<>(map.values());
    }
}