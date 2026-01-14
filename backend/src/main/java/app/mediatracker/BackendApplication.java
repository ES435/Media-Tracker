package app.mediatracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Einstiegspunkt der Spring Boot Anwendung.
 *
 * Zweck: Startet das Backend mit eingebettetem Webserver und lädt alle Beans/Konfigurationen.
 */
@SpringBootApplication
public class BackendApplication {
    /**
     * Startet die Anwendung.
     *
     * Tipp: Für lokale Tests einfach aus der IDE starten. Das statische Test-HTML liegt unter
     * <code>src/main/resources/static/index.html</code> und greift auf den Endpoint <code>/api/search</code> zu.
     */
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}