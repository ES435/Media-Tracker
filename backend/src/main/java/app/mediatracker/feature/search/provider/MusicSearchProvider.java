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
 * Such-Provider für Musik-Inhalte (iTunes Search API).
 * Zweck: Fragt die iTunes-Schnittstelle ab und übersetzt die Ergebnisse in das
 * interne, einheitliche {@link SearchResult}-Format.
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
     * Verhalten: Parst die Antwort und bildet eine Liste normalisierter Treffer.
     * Fehler werden abgefangen; in diesem Fall wird eine leere Liste zurückgegeben.
     *
     * @param searchQuery     Suchbegriff (z. B. Künstlerin, Songtitel)
     * @param limit maximale Anzahl von Treffern
     * @return Liste von {@link SearchResult}
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