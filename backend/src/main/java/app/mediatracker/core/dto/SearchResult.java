package app.mediatracker.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;

/**
 * Einfaches Ergebnisobjekt für die Suche.
 *
 * Zweck: Einheitliches, leichtgewichtiges Format für Treffer verschiedener Medientypen
 * (z. B. "anime", "movie", "book"). Das Frontend kann damit eine Liste anzeigen,
 * ohne die Quell-API kennen zu müssen.
 *
 * Hinweise:
 * - {@code type} kennzeichnet den Medientyp (muss zum Provider passen).
 * - {@code meta} bietet Platz für optionale, API-spezifische Zusatzdaten
 *   (z. B. {"year": 2002, "score": 8.1}).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // null-Felder nicht serialisieren
public class SearchResult {
    private String type;       // z. B. "anime", "movie", "book", "music"
    private String id;         // externe ID/Schlüssel der Quelle
    private String title;      // Titel/Name des Treffers
    private String imageUrl;   // Vorschaubild (optional)
    private String sourceUrl;  // Link zur Detailseite bei der Quelle (optional)

    // API-/Typ-spezifische Extras (optional), frei erweiterbar
    private Map<String, Object> meta;
}