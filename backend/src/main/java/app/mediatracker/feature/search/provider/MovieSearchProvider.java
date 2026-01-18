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
@ConditionalOnProperty(prefix = "search.movie", name = "enabled", havingValue = "true")
public class MovieSearchProvider implements SearchProvider {

    private final IMDbClient imdb;
    private final ObjectMapper mapper;

    /**
     * Konstruktor mit Abhängigkeiten.
     *
     * @param imdb  HTTP-Client für die IMDbAPI
     * @param mapper Jackson-Mapper zum Parsen der JSON-Antworten
     */
    public MovieSearchProvider(IMDbClient imdb, ObjectMapper mapper) {
        this.imdb = imdb;
        this.mapper = mapper;
    }

    /**
     * Liefert den Typnamen dieses Providers.
     *
     * @return "movie"
     */
    @Override
    public String getType() {
        return "movie";
    }

    /**
     * Sucht Filme über die IMDbAPI.
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
            String json = imdb.searchMovieAndSeries(q);
            JsonNode titles = mapper.readTree(json).path("titles");

            List<SearchResult> searchResults = new ArrayList<>();
            for(JsonNode n : titles) {
                if(n.path("type").asText().equals("movie")) {
                    String id    = n.path("id").asText();
                    String title = n.path("primaryTitle").asText("");
                    String img   = n.path("primaryImage").path("url").asText("");
                    String url   = "https://www.imdb.com/title/" + id;

                    // optionale Extras in meta
                    Map<String,Object> meta = new HashMap<>();
                    if (n.hasNonNull("startYear")) meta.put("year", n.get("startYear").asInt());

                    searchResults.add(SearchResult.builder()
                            .type("movie")
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
            log.warn("Movie search failed: {}", e.getMessage());
            return List.of();
        }
    }
}
