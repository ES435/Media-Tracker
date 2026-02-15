package app.mediatracker.feature.search.client.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * HTTP client for the OpenLibrary API.
 *
 * Purpose: Encapsulates HTTP communication and provides a simple method
 * to retrieve book search results as a JSON string.
 *
 * Configuration: Base URL can be overridden via "openlibrary.base-url".
 */
@Component
public class OpenLibraryClient {

    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Erstellt einen Client mit vordefinierter Basis-URL.
     *
     * @param builder von Spring bereitgestellter {@link WebClient.Builder}
     * @param baseUrl Basis-URL der OpenLibrary API (Default: https://openlibrary.org)
     */
    public OpenLibraryClient(WebClient.Builder builder,
                             @Value("${openlibrary.base-url:https://openlibrary.org}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    /**
     * Sucht Bücher nach Titel, optional Autor und ISBN, mit Limit.
     */
    public String searchBook(String query, int limit) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search.json")
                        .queryParam("q", query)
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}