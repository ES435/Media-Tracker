package app.mediatracker.client.movie;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class IMDbClient {

    private final WebClient web;

    public IMDbClient(WebClient.Builder builder,
                      @Value("${imdb.base-url:https://api.imdbapi.dev}") String baseUrl) {
        this.web = builder.baseUrl(baseUrl).build();
    }

    public String searchMovie(String query, int limit) {
        return web.get()
                .uri(u -> u.path("/search/titles").queryParam("query", query).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
