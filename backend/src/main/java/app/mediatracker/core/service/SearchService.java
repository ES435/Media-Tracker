package app.mediatracker.core.service;

import app.mediatracker.core.dto.SearchResult;
import java.util.List;
import java.util.Set;

/**
 * Zentrale Service-Schnittstelle für die Suche über mehrere Medientypen.
 *
 * Zweck: Kapselt die Logik, alle passenden Provider abzufragen und die Ergebnisse
 * in eine gemeinsame Liste zu kombinieren.
 */
public interface SearchService {
    /**
     * Sucht Inhalte über alle passenden Provider.
     *
     * @param q            Suchbegriff
     * @param types        erlaubte Typen (z. B. "anime"); null/leer = alle Typen
     * @param limitPerType maximale Trefferzahl pro Typ/Provider
     * @return kombinierte, nach Einfüge-Reihenfolge sortierte Trefferliste (duplikatbereinigt)
     */
    List<SearchResult> search(String q, Set<String> types, int limitPerType);
}