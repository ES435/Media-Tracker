package app.mediatracker.feature.search.provider;

import app.mediatracker.feature.search.client.book.OpenLibraryClient;
import app.mediatracker.feature.search.core.dto.SearchResult;
import app.mediatracker.feature.search.core.provider.SearchProvider;
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
     * @param searchQuery     Suchbegriff
     * @param limit maximale Anzahl der Treffer
     * @return Liste von {@link SearchResult}
     */
    @Override
    public List<SearchResult> search(String searchQuery, int limit) {
        try {
            // JSON von OpenLibrary abrufen
            String json = client.searchBook(searchQuery, limit);
            JsonNode docs = mapper.readTree(json).path("docs");

            List<SearchResult> results = new ArrayList<>();
            for (JsonNode bookNode : docs) {
                String combined = (bookNode.path("title").asText("") + " " +
                        bookNode.path("subtitle").asText("") + " " +
                        bookNode.path("subject").toString() + " " +
                        bookNode.path("publisher").toString() + " " +
                        bookNode.path("series").toString()).toLowerCase();

                String id = bookNode.path("key").asText();
                String title = bookNode.path("title").asText("");

                // Cover-URL
                String imageUrl = bookNode.has("cover_i")
                        ? "https://covers.openlibrary.org/b/id/" + bookNode.get("cover_i").asInt() + "-L.jpg"
                        : "/images/default-book.png";

                // URL zur Open Library-Seite
                String sourceUrl = "https://openlibrary.org" + id;

                // Meta-Infos (Autoren, Jahr)
                Map<String, Object> meta = new HashMap<>();
                if (bookNode.hasNonNull("author_name")) meta.put("authors", bookNode.get("author_name"));
                if (bookNode.hasNonNull("first_publish_year")) meta.put("year", bookNode.get("first_publish_year").asInt());

                results.add(SearchResult.builder()
                        .type("book")
                        .id(id)
                        .title(title)
                        .imageUrl(imageUrl)
                        .sourceUrl(sourceUrl)
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
