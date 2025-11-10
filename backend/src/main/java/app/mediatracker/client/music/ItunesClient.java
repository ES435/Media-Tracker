package app.mediatracker.client.music;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Minimaler HTTP-Client für die iTunes Search API.
 *
 * Zweck: Stellt eine Methode bereit, um Musiktitel anhand eines Suchbegriffs
 * als rohe JSON-Antwort abzurufen.
 *
 * Konfiguration: Basis-URL kann über "itunes.base-url" überschrieben werden.
 */
@Component
public class ItunesClient {

    private final WebClient web;

    /**
     * Erstellt den Client mit vordefinierter Basis-URL.
     *
     * @param builder von Spring bereitgestellter {@link WebClient.Builder}
     * @param baseUrl Basis-URL der iTunes-API (Default: https://itunes.apple.com)
     */
    public ItunesClient(WebClient.Builder builder,
                        @Value("${itunes.base-url:https://itunes.apple.com}") String baseUrl) {
        this.web = builder.baseUrl(baseUrl).build();
    }

    /**
     * Sucht Musiktitel über die iTunes Search API und liefert die rohe JSON-Antwort.
     *
     * @param term  Suchbegriff
     * @param limit maximale Anzahl der Ergebnisse
     * @return JSON-Antwort als String
     */
    public String searchTracks(String term, int limit) {
        return web.get()
                .uri(u -> u.path("/search")
                        .queryParam("term", term)
                        .queryParam("media", "music")
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}