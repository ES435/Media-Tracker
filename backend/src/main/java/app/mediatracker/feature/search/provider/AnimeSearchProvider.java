package app.mediatracker.feature.search.provider;

import app.mediatracker.feature.search.client.anime.JikanAnimeClient;
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
 * Search provider for anime content (Jikan API for MyAnimeList).
 * Purpose: Calls the Jikan API and maps the results into the internal
 * {@link SearchResult} format so the frontend can render them uniformly.
 * Activation: Loaded only when the property "search.anime.enabled=true" is set.
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "search.anime", name = "enabled", havingValue = "true")
public class AnimeSearchProvider implements SearchProvider {

    private final JikanAnimeClient jikan;
    private final ObjectMapper mapper; // von Spring Boot bereitgestellt

    /**
     * Constructor with dependencies.
     *
     * @param jikan  HTTP client for the Jikan API
     * @param mapper Jackson mapper to parse JSON responses
     */
    public AnimeSearchProvider(JikanAnimeClient jikan, ObjectMapper mapper) {
        this.jikan = jikan;
        this.mapper = mapper;
    }

    /**
     * Returns this provider's media type name.
     *
     * @return "anime"
     */
    @Override public String getType() { return "anime"; }

    /**
     * Searches anime via the Jikan API.
     * Behavior: Parses the response, extracts relevant fields, and returns
     * a normalized list. Errors are logged and result in an empty list.
     *
     * @param searchQuery search term
     * @param limit maximum number of results
     * @return list of {@link SearchResult}
     */
    @Override
    public List<SearchResult> search(String searchQuery, int limit) {
        try {
            String json = jikan.searchAnime(searchQuery);
            JsonNode data = mapper.readTree(json).path("data");

            List<SearchResult> out = new ArrayList<>();
            for (JsonNode animeNode : data) {
                String id    = String.valueOf(animeNode.path("mal_id").asInt());
                String title = animeNode.path("title").asText("");
                String imageUrl   = animeNode.path("images").path("jpg").path("image_url").asText("");
                if (imageUrl.isEmpty()) {
                    imageUrl = animeNode.path("images").path("webp").path("image_url").asText("");
                }
                String url = animeNode.path("url").asText("");

                // optionale Extras in meta
                Map<String,Object> meta = new HashMap<>();
                if (animeNode.hasNonNull("episodes")) meta.put("episodes", animeNode.get("episodes").asInt());
                if (animeNode.hasNonNull("year"))     meta.put("year", animeNode.get("year").asInt());

                out.add(SearchResult.builder()
                        .type("anime")
                        .id(id)
                        .title(title)
                        .imageUrl(imageUrl)
                        .sourceUrl(url)
                        .meta(meta.isEmpty() ? null : meta)
                        .build());

                if (out.size() >= limit) break;
            }
            return out;
        } catch (Exception e) {
            log.warn("Anime search failed: {}", e.getMessage());
            return List.of(); // robust bleiben
        }
    }
}