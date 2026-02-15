package app.mediatracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the Spring Boot application.
 *
 * Purpose: Boots the backend with an embedded web server and loads all beans/configurations.
 */
@SpringBootApplication
public class BackendApplication {
    /**
     * Starts the application.
     *
     * Tip: For local testing, run from the IDE. The static test HTML is located at
     * <code>src/main/resources/static/index.html</code> and calls the endpoint <code>/api/search</code>.
     */
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}