package app.mediatracker.feature.search.provider;

import app.mediatracker.feature.search.client.manga.JikanMangaClient;
import app.mediatracker.feature.search.core.dto.SearchResult;
import app.mediatracker.feature.search.core.provider.SearchProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Such-Provider für Manga-Inhalte (MangaDex API).
 *
 * Zweck: Ruft die MangaDex-API auf und übersetzt die Ergebnisse in das interne
 * {@link SearchResult}-Format.
 *
 * Aktivierung: Wird nur geladen, wenn "search.manga.enabled=true" gesetzt ist.
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "search.manga", name = "enabled", havingValue = "true")
public class MangaSearchProvider implements SearchProvider {

    private final JikanMangaClient jikan;
    private final ObjectMapper mapper;

    public MangaSearchProvider(JikanMangaClient jikan, ObjectMapper mapper) {
        this.jikan = jikan;
        this.mapper = mapper;
    }

    @Override
    public String getType() {
        return "manga";
    }

    @Override
    public List<SearchResult> search(String q, int limit) {
        try {
            String json = jikan.searchManga(q);
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

                Map<String,Object> meta = new HashMap<>();
                if (n.hasNonNull("chapters")) meta.put("chapters", n.get("chapters").asInt());
                if (n.hasNonNull("volumes"))  meta.put("volumes",  n.get("volumes").asInt());
                if (n.hasNonNull("year"))     meta.put("year",     n.get("year").asInt());

                out.add(SearchResult.builder()
                        .type("manga")
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
            log.warn("Manga search failed: {}", e.getMessage());
            return List.of();
        }
    }
}