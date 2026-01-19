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
 * Such-Provider für Anime-Inhalte (Jikan API für MyAnimeList).
 * Zweck: Ruft die Jikan-API auf und übersetzt die Ergebnisse in das interne
 * {@link SearchResult}-Format, damit das Frontend sie einheitlich darstellen kann.
 * Aktivierung: Wird nur geladen, wenn "search.anime.enabled=true" gesetzt ist.
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "search.anime", name = "enabled", havingValue = "true")
public class AnimeSearchProvider implements SearchProvider {

    private final JikanAnimeClient jikan;
    private final ObjectMapper mapper; // von Spring Boot bereitgestellt

    /**
     * Konstruktor mit Abhängigkeiten.
     *
     * @param jikan  HTTP-Client für die Jikan-API
     * @param mapper Jackson-Mapper zum Parsen der JSON-Antworten
     */
    public AnimeSearchProvider(JikanAnimeClient jikan, ObjectMapper mapper) {
        this.jikan = jikan;
        this.mapper = mapper;
    }

    /**
     * Liefert den Typnamen dieses Providers.
     *
     * @return "anime"
     */
    @Override public String getType() { return "anime"; }

    /**
     * Sucht Anime über die Jikan-API.
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