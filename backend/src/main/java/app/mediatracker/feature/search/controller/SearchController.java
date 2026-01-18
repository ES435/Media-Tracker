package app.mediatracker.feature.search.controller;

import app.mediatracker.feature.search.core.dto.SearchResult;
import app.mediatracker.feature.search.core.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST-Controller für die Suche.
 *
 * Zweck: Nimmt HTTP-Anfragen vom Frontend entgegen, bereitet Parameter auf
 * und ruft den Such-Service auf.
 *
 * Pfad: GET /api/search
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Führt eine Suche aus.
     *
     * Beispiel: /api/search?q=naruto&types=anime,movie&limit=24
     *
     * @param q     Suchbegriff (z. B. "Naruto")
     * @param types Komma‑getrennte Liste von Typen (z. B. "anime").
     *              Leer oder nicht gesetzt = alle Provider.
     * @param limit Maximale Treffer pro Typ. Standard ist 24.
     * @return Liste kombinierter Suchergebnisse als JSON.
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