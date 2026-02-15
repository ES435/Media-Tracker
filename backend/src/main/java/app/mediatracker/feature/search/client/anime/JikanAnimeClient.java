package app.mediatracker.feature.search.client.anime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Lightweight HTTP client for the Jikan API (MyAnimeList proxy).
 *
 * Purpose: Encapsulates HTTP communication and provides a simple method
 * to retrieve anime search results as a JSON string.
 *
 * Configuration: Base URL can be overridden via the property "jikan.base-url".
 */
@Component
public class JikanAnimeClient {

    private final WebClient web;

    /**
     * Creates a client with a predefined base URL.
     *
     * @param builder Spring-provided {@link WebClient.Builder}
     * @param baseUrl base URL of the Jikan API (default: https://api.jikan.moe/v4)
     */
    public JikanAnimeClient(WebClient.Builder builder,
                            @Value("${jikan.base-url:https://api.jikan.moe/v4}") String baseUrl) {
        this.web = builder.baseUrl(baseUrl).build();
    }

    /**
     * Sucht Anime bei Jikan und liefert die rohe JSON-Antwort.
     *
     * Hinweis: Blockiert den aufrufenden Thread bis zur Antwort (vereinfachte Nutzung).
     *
     * @param query Suchbegriff
     * @return JSON als String
     */
    public String searchAnime(String query) {
        return web.get()
                .uri(u -> u.path("/anime").queryParam("q", query).build())
                .retrieve()
                .bodyToMono(String.class)
                .block(); // simpel halten
    }
}