package app.mediatracker.feature.search.client.anime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Leichter HTTP-Client für die Jikan API (MyAnimeList-Proxy).
 *
 * Zweck: Kapselt die HTTP-Kommunikation und bietet eine einfache Methode,
 * um Anime-Suche als JSON-String abzurufen.
 *
 * Konfiguration: Basis-URL kann über "jikan.base-url" überschrieben werden.
 */
@Component
public class JikanAnimeClient {

    private final WebClient web;

    /**
     * Erstellt einen Client mit vordefinierter Basis-URL.
     *
     * @param builder von Spring bereitgestellter {@link WebClient.Builder}
     * @param baseUrl Basis-URL der Jikan-API (Default: https://api.jikan.moe/v4)
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
     * @param q Suchbegriff
     * @return JSON als String
     */
    public String searchAnime(String q) {
        return web.get()
                .uri(u -> u.path("/anime").queryParam("q", q).build())
                .retrieve()
                .bodyToMono(String.class)
                .block(); // simpel halten
    }
}