package app.mediatracker.search.provider;

import app.mediatracker.client.anime.JikanClient;
import app.mediatracker.core.dto.SearchResult;
import app.mediatracker.core.provider.SearchProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "search.anime", name = "enabled", havingValue = "true")
public class AnimeSearchProvider implements SearchProvider {

    private final JikanClient jikan;
    private final ObjectMapper mapper; // von Spring Boot bereitgestellt

    public AnimeSearchProvider(JikanClient jikan, ObjectMapper mapper) {
        this.jikan = jikan;
        this.mapper = mapper;
    }

    @Override public String getType() { return "anime"; }

    @Override
    public List<SearchResult> search(String q, int limit) {
        try {
            String json = jikan.searchAnime(q);
            JsonNode data = mapper.readTree(json).path("data");

            List<SearchResult> out = new ArrayList<>();
            for (JsonNode n : data) {
                String id    = String.valueOf(n.path("mal_id").asInt());
                String title = n.path("title").asText("");
                String img   = n.path("images").path("jpg").path("image_url").asText("");
                if (img.isEmpty()) {
                    img = n.path("images").path("webp").path("image_url").asText("");
                }
                String url   = n.path("url").asText("");

                // optionale Extras in meta
                Map<String,Object> meta = new HashMap<>();
                if (n.hasNonNull("episodes")) meta.put("episodes", n.get("episodes").asInt());
                if (n.hasNonNull("year"))     meta.put("year", n.get("year").asInt());

                out.add(SearchResult.builder()
                        .type("anime")
                        .id(id)
                        .title(title)
                        .imageUrl(img)
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