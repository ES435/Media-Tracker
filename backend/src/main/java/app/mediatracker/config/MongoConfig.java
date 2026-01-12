package app.mediatracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * MongoDB Konfiguration.
 * <p>
 * Aktiviert Mongo Auditing über {@link EnableMongoAuditing}, sodass Felder mit
 * {@link org.springframework.data.annotation.CreatedDate} und
 * {@link org.springframework.data.annotation.LastModifiedDate}
 * automatisch von Spring Data gesetzt bzw. aktualisiert werden.
 * Dies betrifft z. B. {@code User.createdAt/updatedAt} und
 * {@code UserLibraryEntry.createdAt/updatedAt}.
 * </p>
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
}
