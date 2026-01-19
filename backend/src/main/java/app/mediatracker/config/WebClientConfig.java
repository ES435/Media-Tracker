package app.mediatracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Konfiguration für HTTP-Clients.
 *
 * Zweck: Stellt einen vorkonfigurierten {@link WebClient.Builder} bereit,
 * damit Clients (z. B. API-Clients) einheitliche Defaults verwenden können.
 */
@Configuration
public class WebClientConfig {
    /**
     * Gemeinsamer WebClient-Builder.
     *
     * Hinweis: Setzt einen sprechenden User-Agent für externe APIs.
     * Weitere Defaults (Timeouts, Logging, Proxy) können hier zentral ergänzt werden.
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.USER_AGENT, "MediaTracker/1.0 (+localhost)");
    }
}