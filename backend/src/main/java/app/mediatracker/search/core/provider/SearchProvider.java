package app.mediatracker.search.core.provider;

import app.mediatracker.search.core.dto.SearchResult;
import java.util.List;

/**
 * Basis-Schnittstelle für Such-Provider eines Medientyps.
 *
 * Zweck: Jeder Provider kümmert sich um genau einen Typ (z. B. "anime")
 * und weiß, wie man eine externe Quelle abfragt und die Ergebnisse
 * in unser internes Format {@link SearchResult} übersetzt.
 *
 * Erweiterbarkeit: Um einen neuen Medientyp zu unterstützen, einfach eine neue
 * Implementierung dieser Schnittstelle erstellen, als Spring-Bean annotieren
 * (z. B. mit {@code @Component}) und in {@link #getType()} den Typnamen zurückgeben.
 */
public interface SearchProvider {
    /**
     * Eindeutiger Typname, den dieser Provider liefert (z. B. "anime", "movie").
     */
    String getType();

    /**
     * Führt die Suche beim jeweiligen externen Dienst aus und liefert normalisierte Ergebnisse.
     *
     * @param q     Suchbegriff
     * @param limit maximale Anzahl der Treffer
     * @return Liste von Suchergebnissen
     */
    List<SearchResult> search(String q, int limit);
}