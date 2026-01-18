package app.mediatracker.feature.search.provider;

import app.mediatracker.feature.search.client.game.RawgClient;
import app.mediatracker.feature.search.core.dto.SearchResult;
import app.mediatracker.feature.search.core.provider.SearchProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Such-Provider für Game-Inhalte (Rawg API).*
 * Zweck: Ruft die Rawg-API auf und übersetzt die Ergebnisse in das interne
 * {@link SearchResult}-Format, damit das Frontend sie einheitlich darstellen kann.

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
     * Sucht Games über die Rawg-API.*
     * Verhalten: Parst die Antwort, extrahiert relevante Felder und liefert
     * eine normalisierte Liste. Fehler werden geloggt und führen zu einer leeren Liste.
     *
     * @param searchQuery     Suchbegriff
     * @param limit maximale Anzahl der Treffer
     * @return Liste von {@link SearchResult}
     */
    @Override
    public List<SearchResult> search(String searchQuery, int limit) {
        try {
            String json = rawg.searchGame(searchQuery);
            JsonNode results = mapper.readTree(json).path("results");

            List<SearchResult> out = new ArrayList<>();
            for (JsonNode gameNode : results) {
                String id    = String.valueOf(gameNode.path("id").asInt());
                String title = gameNode.path("name").asText("");
                String imageUrl   = gameNode.path("background_image").asText("");

                // optionale Extras in meta
                Map<String,Object> meta = new HashMap<>();
                if (gameNode.hasNonNull("platforms")) meta.put("platforms", gameNode.get("platforms"));
                if (gameNode.hasNonNull("released"))     meta.put("released", gameNode.get("released"));

                out.add(SearchResult.builder()
                        .type("game")
                        .id(id)
                        .title(title)
                        .imageUrl(imageUrl)
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
