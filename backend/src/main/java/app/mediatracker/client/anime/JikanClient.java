package app.mediatracker.client.anime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class JikanClient {

    private final WebClient web;

    public JikanClient(WebClient.Builder builder,
                       @Value("${jikan.base-url:https://api.jikan.moe/v4}") String baseUrl) {
        this.web = builder.baseUrl(baseUrl).build();
    }

    public String searchAnime(String q) {
        return web.get()
                .uri(u -> u.path("/anime").queryParam("q", q).build())
                .retrieve()
                .bodyToMono(String.class)
                .block(); // simpel halten
    }
}