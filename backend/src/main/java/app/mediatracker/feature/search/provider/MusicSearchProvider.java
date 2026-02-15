package app.mediatracker.feature.search.provider;

import app.mediatracker.feature.search.client.music.ItunesClient;
import app.mediatracker.feature.search.core.dto.SearchResult;
import app.mediatracker.feature.search.core.provider.SearchProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Search provider for music content (iTunes Search API).
 *
 * Purpose: Queries the iTunes interface and translates the results into the
 * internal, unified {@link SearchResult} format.
 *
 * Activation: This provider is only activated if the property
 * "search.music.enabled=true" is set in the configuration (see {@link ConditionalOnProperty}).
 */
@Component
@ConditionalOnProperty(prefix = "search.music", name = "enabled", havingValue = "true")
public class MusicSearchProvider implements SearchProvider {

    private final ItunesClient itunes;
    private final ObjectMapper mapper;

    /**
     * Creates the provider.
     *
     * @param itunes  HTTP client for iTunes
     * @param mapper  Jackson Mapper for parsing JSON responses
     */
    public MusicSearchProvider(ItunesClient itunes, ObjectMapper mapper) {
        this.itunes = itunes;
        this.mapper = mapper;
    }

    /**
     * Returns the type name of this provider.
     *
     * @return "music"
     */
    @Override public String getType() { return "music"; }

    /**
     * Searches for music tracks via the iTunes Search API.
     *
     * Behavior: Parses the response and forms a list of normalized hits.
     * Errors are caught; in this case, an empty list is returned.
     *
     * @param searchQuery     search term (e.g. artist, song title)
     * @param limit           maximum number of hits
     * @return List of {@link SearchResult}
     */
    @Override
    public List<SearchResult> search(String searchQuery, int limit) {
        try {
            String json = itunes.searchTracks(searchQuery, limit);
            JsonNode results = mapper.readTree(json).path("results");

            List<SearchResult> out = new ArrayList<>();
            for (JsonNode musicNode : results) {
                String id     = musicNode.path("trackId").asText("");
                String title  = musicNode.path("trackName").asText("");
                String artist = musicNode.path("artistName").asText("");
                String imageUrl    = musicNode.path("artworkUrl100").asText("");
                String sourceUrl    = musicNode.path("trackViewUrl").asText("");

                Map<String,Object> meta = new HashMap<>();
                if (!artist.isEmpty()) meta.put("artist", artist);
                if (musicNode.hasNonNull("collectionName")) meta.put("album", musicNode.get("collectionName").asText());
                if (musicNode.hasNonNull("previewUrl"))     meta.put("previewUrl", musicNode.get("previewUrl").asText());

                out.add(SearchResult.builder()
                        .type("music").id(id)
                        .title(title)
                        .imageUrl(imageUrl)
                        .sourceUrl(sourceUrl)
                        .meta(meta.isEmpty() ? null : meta)
                        .build());

                if (out.size() >= limit) break;
            }
            return out;
        } catch (Exception e) {
            return List.of();
        }
    }
}