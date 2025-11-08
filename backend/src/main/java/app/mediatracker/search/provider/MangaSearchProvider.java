package app.mediatracker.search.provider;

import app.mediatracker.client.manga.MangaDexClient;
import app.mediatracker.core.dto.SearchResult;
import app.mediatracker.core.provider.SearchProvider;
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

    private final MangaDexClient mangaDex;
    private final ObjectMapper mapper;

    public MangaSearchProvider(MangaDexClient mangaDex, ObjectMapper mapper) {
        this.mangaDex = mangaDex;
        this.mapper   = mapper;
    }

    @Override
    public String getType() { return "manga"; }

    @Override
    public List<SearchResult> search(String q, int limit) {
        try {
            String json = mangaDex.searchManga(q, limit);
            JsonNode data = mapper.readTree(json).path("data");

            List<SearchResult> out = new ArrayList<>();
            for (JsonNode n : data) {
                String id = n.path("id").asText();
                JsonNode attrs = n.path("attributes");

                String title = "";
                JsonNode titles = attrs.path("title");
                if (titles.has("en")) title = titles.path("en").asText();
                else if (titles.elements().hasNext())
                    title = titles.elements().next().asText();

                String desc = attrs.path("description").path("en").asText("");
                int year = attrs.path("year").asInt(0);


                String coverArt = "";
                if (n.has("relationships")) {
                    for (JsonNode rel : n.get("relationships")) {
                        if (rel.path("type").asText("").equals("cover_art")) {
                            coverArt = rel.path("attributes").path("fileName").asText("");
                        }
                    }
                }

                // URL zur MangaDex-Seite
                String url = "https://mangadex.org/title/" + id;

                Map<String,Object> meta = new HashMap<>();
                if (year > 0) meta.put("year", year);
                if (!desc.isEmpty()) meta.put("description", desc);

                out.add(SearchResult.builder()
                        .type("manga")
                        .id(id)
                        .title(title)
                        .imageUrl(coverArt.isEmpty() ? null :
                                "https://uploads.mangadex.org/covers/" + id + "/" + coverArt)
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
