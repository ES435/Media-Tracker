package app.mediatracker.feature.search.provider;

import app.mediatracker.feature.search.client.book.OpenLibraryClient;
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
 * Search provider for book content (OpenLibrary API).
 * Purpose: Calls OpenLibrary and maps results into the internal {@link SearchResult}
 * format so the frontend can render them uniformly.
 * Activation: Loaded only when the property "search.book.enabled=true" is set.
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
     * Returns this provider's media type name.
     *
     * @return "book"
     */
    @Override
    public String getType() {
        return "book";
    }

    /**
     * Searches books via the OpenLibrary API.
     * Behavior: Parses the response, extracts relevant fields, and returns
     * a normalized list. Errors are logged and result in an empty list.
     *
     * @param searchQuery search term
     * @param limit maximum number of results
     * @return list of {@link SearchResult}
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
