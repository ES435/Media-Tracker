package app.mediatracker.feature.search.core.service;

import app.mediatracker.feature.search.core.dto.SearchResult;

import java.util.List;
import java.util.Set;

/**
 * Central service interface for searching across multiple media types.
 * Purpose: Encapsulates the logic to query all matching providers and
 * combine their results into a single list.
 */
public interface SearchService {
    /**
     * Searches content across all matching providers.
     *
     * @param searchQuery search term
     * @param types allowed media types (e.g., "anime"); null/empty = all types
     * @param limitPerType maximum number of results per type/provider
     * @return combined list of results in insertion order (duplicates removed)
     */
    List<SearchResult> search(String searchQuery, Set<String> types, int limitPerType);
}