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
 * Search provider for game content (RAWG API).
 *
 * Purpose: Calls the RAWG API and translates the results into the internal
 * {@link SearchResult} format so the frontend can display them uniformly.
 *
 * Activation: Only loaded if "search.game.enabled=true" is set.
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "search.game", name = "enabled", havingValue = "true")
public class GameSearchProvider implements SearchProvider {

    private final RawgClient rawg;
    private final ObjectMapper mapper; // provided by Spring Boot

    /**
     * Constructor with dependencies.
     *
     * @param rawg   HTTP client for the RAWG API
     * @param mapper Jackson Mapper for parsing JSON responses
     */
    public GameSearchProvider(RawgClient rawg, ObjectMapper mapper) {
        this.rawg = rawg;
        this.mapper = mapper;
    }

    /**
     * Returns the type name of this provider.
     *
     * @return "game"
     */
    @Override public String getType() { return "game"; }

    /**
     * Searches for games via the RAWG API.
     *
     * Behavior: Parses the response, extracts relevant fields, and returns
     * a normalized list. Errors are logged and result in an empty list.
     *
     * @param searchQuery the search term
     * @param limit       maximum number of results
     * @return List of {@link SearchResult}
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

                // optional extras in meta
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
            return List.of(); // fail gracefully
        }
    }
}