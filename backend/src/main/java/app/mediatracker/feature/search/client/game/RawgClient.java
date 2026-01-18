package app.mediatracker.feature.search.client.game;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Minimaler HTTP-Client für die rawg game Search API.
 *
 * Zweck: Stellt eine Methode bereit, um Game-Titel anhand eines Suchbegriffs
 * als rohe JSON-Antwort abzurufen.
 *
 * Konfiguration: Basis-URL kann über "rawg.base-url" überschrieben werden.
 */
@Component
public class RawgClient {

    private final WebClient web;
    private final String apiKey;

    /**
     * Erstellt den Client mit vordefinierter Basis-URL.
     *
     * @param builder von Spring bereitgestellter {@link WebClient.Builder}
     * @param baseUrl Basis-URL der rawg-API (Default: https://rawg.io/api)
     */
    public RawgClient(WebClient.Builder builder,
                        @Value("${rawg.base-url:https://rawg.io/api}") String baseUrl,
                        @Value("${rawg.api-key:}") String apiKey ) {
        this.web = builder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    System.out.println("RAWG API KEY: " + apiKey);
    }

    /**
     * Sucht Games bei Rawg und liefert die rohe JSON-Antwort.
     *
     * Hinweis: 
     *
     * @param q Suchbegriff
     * @return JSON als String
     */
    public String searchGame(String q) {
        return web.get()
                .uri(u -> u.path("/games")
                    .queryParam("search", q)
                    .queryParam("key", apiKey)
                    .build())
                .retrieve()
                .bodyToMono(String.class)
                .block(); // simpel halten
    }
}