package app.mediatracker.search.provider;

import app.mediatracker.client.game.RawgClient;
import app.mediatracker.core.dto.SearchResult;
import app.mediatracker.core.provider.SearchProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Such-Provider für Game-Inhalte (Rawg API).
 *
 * Zweck: Ruft die Rawg-API auf und übersetzt die Ergebnisse in das interne
 * {@link SearchResult}-Format, damit das Frontend sie einheitlich darstellen kann.
 *
 * Aktivierung: Wird nur geladen, wenn "search.game.enabled=true" gesetzt ist.
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "search.anime", name = "enabled", havingValue = "true")
public class GameSearchProvider implements SearchProvider {

    private final RawgClient rawg;
    private final ObjectMapper mapper; // von Spring Boot bereitgestellt

    /**
     * Konstruktor mit Abhängigkeiten.
     *
     * @param jikan  HTTP-Client für die Jikan-API
     * @param mapper Jackson-Mapper zum Parsen der JSON-Antworten
     */
    public GameSearchProvider(RawgClient rawg, ObjectMapper mapper) {
        this.rawg = rawg;
        this.mapper = mapper;
    }

    /**
     * Liefert den Typnamen dieses Providers.
     *
     * @return "game"
     */
    @Override public String getType() { return "game"; }

    /**
     * Sucht Games über die Rawg-API.
     *
     * Verhalten: Parst die Antwort, extrahiert relevante Felder und liefert
     * eine normalisierte Liste. Fehler werden geloggt und führen zu einer leeren Liste.
     *
     * @param q     Suchbegriff
     * @param limit maximale Anzahl der Treffer
     * @return Liste von {@link SearchResult}
     */
    @Override
    public List<SearchResult> search(String q, int limit) {
        try {
            String json = rawg.searchGame(q);
            JsonNode results = mapper.readTree(json).path("results");

            List<SearchResult> out = new ArrayList<>();
            for (JsonNode n : results) {
                String id    = String.valueOf(n.path("id").asInt());
                String title = n.path("name").asText("");
                String img   = n.path("background_image").asText("");

                // optionale Extras in meta
                Map<String,Object> meta = new HashMap<>();
                if (n.hasNonNull("platforms")) meta.put("platforms", n.get("platforms"));
                if (n.hasNonNull("released"))     meta.put("released", n.get("released"));

                out.add(SearchResult.builder()
                        .type("game")
                        .id(id)
                        .title(title)
                        .imageUrl(img)
                        .meta(meta.isEmpty() ? null : meta)
                        .build());

                if (out.size() >= limit) break;
            }
            return out;
        } catch (Exception e) {
            log.warn("Game search failed: {}", e.getMessage());
            return List.of(); // robust bleiben
        }
    }
}
