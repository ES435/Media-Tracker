package app.mediatracker.search.provider;

import app.mediatracker.client.book.OpenLibraryClient;
import app.mediatracker.core.dto.SearchResult;
import app.mediatracker.core.provider.SearchProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Such-Provider für Buch-Inhalte (OpenLibrary API).
 *
 * Zweck: Ruft die OpenLibrary API) auf und übersetzt die Ergebnisse in das interne
 * {@link SearchResult}-Format, damit das Frontend sie einheitlich darstellen kann.
 *
 * Aktivierung: Wird nur geladen, wenn "search.book.enabled=true" gesetzt ist.
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "search.book", name = "enabled", havingValue = "true")
public class BookSearchProvider implements SearchProvider {

    private final OpenLibraryClient client;
    private final ObjectMapper mapper;

    public BookSearchProvider(OpenLibraryClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    /**
     * Liefert den Typnamen dieses Providers.
     *
     * @return "book"
     */
    @Override
    public String getType() {
        return "book";
    }

    /**
     * Sucht Bücher über die OpenLibrary API.
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
            // JSON von OpenLibrary abrufen
            String json = client.searchBook(q, limit);
            JsonNode docs = mapper.readTree(json).path("docs");

            List<SearchResult> results = new ArrayList<>();
            for (JsonNode n : docs) {
                String combined = (n.path("title").asText("") + " " +
                        n.path("subtitle").asText("") + " " +
                        n.path("subject").toString() + " " +
                        n.path("publisher").toString() + " " +
                        n.path("series").toString()).toLowerCase();

                String id = n.path("key").asText();
                String title = n.path("title").asText("");

                // Cover-URL
                String img = n.has("cover_i")
                        ? "https://covers.openlibrary.org/b/id/" + n.get("cover_i").asInt() + "-L.jpg"
                        : "/images/default-book.png";

                // URL zur Open Library-Seite
                String url = "https://openlibrary.org" + id;

                // Meta-Infos (Autoren, Jahr)
                Map<String, Object> meta = new HashMap<>();
                if (n.hasNonNull("author_name")) meta.put("authors", n.get("author_name"));
                if (n.hasNonNull("first_publish_year")) meta.put("year", n.get("first_publish_year").asInt());

                results.add(SearchResult.builder()
                        .type("book")
                        .id(id)
                        .title(title)
                        .imageUrl(img)
                        .sourceUrl(url)
                        .meta(meta.isEmpty() ? null : meta)
                        .build());

                if (results.size() >= limit) break;
            }
            return results;

        } catch (Exception e) {
            log.warn("Book search failed: {}", e.getMessage());
            return List.of();
        }
    }
}
