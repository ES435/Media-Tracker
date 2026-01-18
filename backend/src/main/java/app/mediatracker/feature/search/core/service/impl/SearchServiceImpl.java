package app.mediatracker.feature.search.core.service.impl;

import app.mediatracker.feature.search.core.dto.SearchResult;
import app.mediatracker.feature.search.core.provider.SearchProvider;
import app.mediatracker.feature.search.core.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementierung des Such-Services.
 * Zweck: Fragt alle registrierten {@link SearchProvider} ab, filtert optional nach Typen
 * und führt die Ergebnisse in einer Liste zusammen. Doppelte Einträge (gleiche Kombination
 * aus {@code type} und {@code id}) werden entfernt.
 */
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    /**
     * Von Spring injizierte Liste aller verfügbaren Provider (eine Bean pro Medientyp).
     */
    private final List<SearchProvider> providers;

    /**
     * Sucht über alle passenden Provider und kombiniert die Ergebnisse.
     * Verhalten:
     * - Wenn {@code types} leer oder {@code null} ist, werden alle Provider verwendet.
     * - Pro Provider wird mit {@code limitPerType} begrenzt.
     * - Duplikate werden anhand von {@code type#id} entfernt (stabile Einfüge-Reihenfolge).
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