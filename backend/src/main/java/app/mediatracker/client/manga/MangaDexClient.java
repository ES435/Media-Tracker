package app.mediatracker.client.manga;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * Minimaler HTTP-Client für die MangaDex v5 API.
 * Zweck: Suche nach Manga + Ermittlung der Bild-URLs für ein Kapitel (data-saver).
 * Konfiguration: via "mangadex.base-url" überschreibbar.
 */
@Component
public class MangaDexClient {

    private final WebClient web;

    public MangaDexClient(WebClient.Builder builder,
                          @Value("${mangadex.base-url:https://api.mangadex.org}") String baseUrl) {
        this.web = builder
                .baseUrl(baseUrl)
                .defaultHeader("User-Agent", "MediaTracker/1.0")
                .build();
    }

    /** Suche Manga-Titel (ohne Auth). */
    public String searchManga(String title, int limit) {
        return web.get()
                .uri(u -> u.path("/manga")
                        .queryParam("title", title)
                        .queryParam("limit", limit)
                        .queryParam("includes[]", "cover_art") //wichtig für Cover
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    /**
     * Bild-URLs für ein Kapitel (data-saver).
     * Hinweis: MangaDex liefert Dateien/Hash in /chapter, den Host in /at-home/server.
     */
    public List<String> getChapterImageUrls(String chapterId, boolean dataSaver) {
        Map<String, Object> chapter = web.get()
                .uri("/chapter/{id}", chapterId)
                .retrieve()
                .bodyToMono(Map.class).block();

        Map<String, Object> data = (Map<String, Object>) chapter.get("data");
        Map<String, Object> attr = (Map<String, Object>) data.get("attributes");
        String hash = (String) attr.get("hash");
        List<String> files = (List<String>) attr.get(dataSaver ? "dataSaver" : "data");

        Map<String, Object> home = web.get()
                .uri("/at-home/server/{id}", chapterId)
                .retrieve()
                .bodyToMono(Map.class).block();

        String baseUrl = (String) home.get("baseUrl");
        String mode = dataSaver ? "data-saver" : "data";

        return files.stream()
                .map(f -> baseUrl + "/" + mode + "/" + hash + "/" + f)
                .toList();
    }
}
