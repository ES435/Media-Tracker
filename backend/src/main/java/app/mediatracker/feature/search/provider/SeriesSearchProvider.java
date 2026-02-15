package app.mediatracker.feature.search.provider;

import app.mediatracker.feature.search.client.movie_and_series.IMDbClient;
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

@Slf4j
@Component
@ConditionalOnProperty(prefix = "search.series", name = "enabled", havingValue = "true")
public class SeriesSearchProvider implements SearchProvider {
    private final IMDbClient imdb;
    private final ObjectMapper mapper;

    /**
     * Constructor with dependencies.
     *
     * @param imdb   HTTP client for the IMDb API
     * @param mapper Jackson Mapper for parsing JSON responses
     */
    public SeriesSearchProvider(IMDbClient imdb, ObjectMapper mapper) {
        this.imdb = imdb;
        this.mapper = mapper;
    }

    /**
     * Returns the type name of this provider.
     *
     * @return "series"
     */
    @Override
    public String getType() {
        return "series";
    }

    /**
     * Searches for series via the IMDb API.
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
            String json = imdb.searchMovieAndSeries(searchQuery);
            JsonNode titles = mapper.readTree(json).path("titles");

            List<SearchResult> searchResults = new ArrayList<>();
            for(JsonNode seriesNode : titles) {
                if(seriesNode.path("type").asText().equals("tvSeries")) {
                    String id    = seriesNode.path("id").asText();
                    String title = seriesNode.path("primaryTitle").asText("");
                    String img   = seriesNode.path("primaryImage").path("url").asText("");
                    String url   = "https://www.imdb.com/title/" + id;

                    // optional extras in meta
                    Map<String,Object> meta = new HashMap<>();
                    if (seriesNode.hasNonNull("startYear")) meta.put("year", seriesNode.get("startYear").asInt());

                    searchResults.add(SearchResult.builder()
                            .type("series")
                            .id(id)
                            .title(title)
                            .imageUrl(img)
                            .sourceUrl(url)
                            .meta(meta.isEmpty() ? null : meta)
                            .build());

                    if (searchResults.size() >= limit) break;
                }
            }

            return searchResults;
        } catch (Exception e) {
            log.warn("Series search failed: {}", e.getMessage());
            return List.of();
        }
    }
}