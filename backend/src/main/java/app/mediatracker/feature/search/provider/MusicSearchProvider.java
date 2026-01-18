package app.mediatracker.feature.search.provider;

import app.mediatracker.feature.search.client.music.ItunesClient;
import app.mediatracker.feature.search.core.dto.SearchResult;
import app.mediatracker.feature.search.core.provider.SearchProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Such-Provider für Musik-Inhalte (iTunes Search API).
 *
 * Zweck: Fragt die iTunes-Schnittstelle ab und übersetzt die Ergebnisse in das
 * interne, einheitliche {@link SearchResult}-Format.
 *
 * Aktivierung: Dieser Provider wird nur aktiviert, wenn in der Konfiguration
 * die Eigenschaft "search.music.enabled=true" gesetzt ist (siehe {@link ConditionalOnProperty}).
 */
@Component
@ConditionalOnProperty(prefix = "search.music", name = "enabled", havingValue = "true")
public class MusicSearchProvider implements SearchProvider {

    private final ItunesClient itunes;
    private final ObjectMapper mapper;

    /**
     * Erstellt den Provider.
     *
     * @param itunes  HTTP-Client für iTunes
     * @param mapper  Jackson-Mapper zum Parsen der JSON-Antworten
     */
    public MusicSearchProvider(ItunesClient itunes, ObjectMapper mapper) {
        this.itunes = itunes;
        this.mapper = mapper;
    }

    /**
     * Liefert den Typnamen dieses Providers.
     *
     * @return "music"
     */
    @Override public String getType() { return "music"; }

    /**
     * Sucht Musiktitel über die iTunes Search API.
     *
     * Verhalten: Parst die Antwort und bildet eine Liste normalisierter Treffer.
     * Fehler werden abgefangen; in diesem Fall wird eine leere Liste zurückgegeben.
     *
     * @param q     Suchbegriff (z. B. Künstlerin, Songtitel)
     * @param limit maximale Anzahl von Treffern
     * @return Liste von {@link SearchResult}
     */
    @Override
    public List<SearchResult> search(String q, int limit) {
        try {
            String json = itunes.searchTracks(q, limit);
            JsonNode results = mapper.readTree(json).path("results");

            List<SearchResult> out = new ArrayList<>();
            for (JsonNode n : results) {
                String id     = n.path("trackId").asText("");
                String title  = n.path("trackName").asText("");
                String artist = n.path("artistName").asText("");
                String art    = n.path("artworkUrl100").asText("");
                String src    = n.path("trackViewUrl").asText("");

                Map<String,Object> meta = new HashMap<>();
                if (!artist.isEmpty()) meta.put("artist", artist);
                if (n.hasNonNull("collectionName")) meta.put("album", n.get("collectionName").asText());
                if (n.hasNonNull("previewUrl"))     meta.put("previewUrl", n.get("previewUrl").asText());

                out.add(SearchResult.builder()
                        .type("music").id(id).title(title).imageUrl(art).sourceUrl(src)
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