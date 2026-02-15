package app.mediatracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration for HTTP clients.
 *
 * Purpose: Exposes a preconfigured {@link WebClient.Builder} so HTTP clients (e.g., API clients)
 * can share consistent defaults.
 */
@Configuration
public class WebClientConfig {
    /**
     * Shared WebClient builder.
     *
     * Note: Sets a descriptive User-Agent for external APIs.
     * Additional defaults (timeouts, logging, proxy) can be configured here centrally.
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.USER_AGENT, "MediaTracker/1.0 (+localhost)");
    }
}