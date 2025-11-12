package app.mediatracker.client.movie;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Leichter HTTP-Client für die IMDb-API.
 *
 * Zweck: Kapselt die HTTP-Kommunikation und bietet eine einfache Methode,
 * um Film-Suche als JSON-String abzurufen.
 *
 * Konfiguration: Basis-URL kann über "imdb.base-url" überschrieben werden.
 */
@Component
public class IMDbClient {

    private final WebClient web;

    /**
     * Erstellt einen Client mit vordefinierter Basis-URL.
     *
     * @param builder von Spring bereitgestellter {@link WebClient.Builder}
     * @param baseUrl Basis-URL der IMDb-API (Default: https://api.imdbapi.dev)
     */
    public IMDbClient(WebClient.Builder builder,
                      @Value("${imdb.base-url:https://api.imdbapi.dev}") String baseUrl) {
        this.web = builder.baseUrl(baseUrl).build();
    }

    /**
     * Sucht Filme bei IMDb und liefert die rohe JSON-Antwort.
     *
     * Hinweis: Blockiert den aufrufenden Thread bis zur Antwort (vereinfachte Nutzung).
     *
     * @param query Suchbegriff
     * @return JSON als String
     */
    @Cacheable("IMDbSearch")
    public String searchMovie(String query, int limit) {
        return web.get()
                .uri(u -> u.path("/search/titles")
                    .queryParam("query", query)
                    .queryParam("limit", limit)
                    .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
