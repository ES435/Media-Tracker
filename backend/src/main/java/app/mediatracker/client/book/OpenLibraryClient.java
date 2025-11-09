package app.mediatracker.client.book;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * HTTP-Client für die OpenLibrary API.
 *
 * Zweck: Kapselt die HTTP-Kommunikation und bietet eine einfache Methode,
 * um Bücher-Suche als JSON-String abzurufen.
 *
 * Konfiguration: Basis-URL kann über "openlibrary.base-url" überschrieben werden.
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