package app.mediatracker.client.manga;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Leichter HTTP-Client für die Jikan API (MyAnimeList-Proxy).
 *
 * Zweck: Kapselt die HTTP-Kommunikation und bietet eine einfache Methode,
 * um Manga-Suche als JSON-String abzurufen.
 *
 * Konfiguration: Basis-URL kann über "jikan.base-url" überschrieben werden.
 */
@Component
public class JikanMangaClient {

    private final WebClient web;

    /**
     * Erstellt einen Client mit vordefinierter Basis-URL.
     *
     * @param builder von Spring bereitgestellter {@link WebClient.Builder}
     * @param baseUrl Basis-URL der Jikan-API (Default: https://api.jikan.moe/v4)
     */
    public JikanMangaClient(WebClient.Builder builder,
                            @Value("${jikan.base-url:https://api.jikan.moe/v4}") String baseUrl) {
        this.web = builder.baseUrl(baseUrl).build();
    }

    /**
     * Sucht Manga bei Jikan und liefert die rohe JSON-Antwort.
     *
     * Hinweis: Blockiert den aufrufenden Thread bis zur Antwort (vereinfachte Nutzung).
     *
     * @param query Suchbegriff
     * @return JSON als String
     */

    public String searchManga(String query) {
        return web.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/manga")
                        .queryParam("q", query)
                        .queryParam("limit", 10)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}