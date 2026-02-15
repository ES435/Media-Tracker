package app.mediatracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * MongoDB configuration.
 * <p>
 * Enables Mongo auditing via {@link EnableMongoAuditing} so fields annotated with
 * {@link org.springframework.data.annotation.CreatedDate} and
 * {@link org.springframework.data.annotation.LastModifiedDate}
 * are automatically populated and updated by Spring Data.
 * This applies to e.g. {@code User.createdAt/updatedAt} and
 * {@code UserLibraryEntry.createdAt/updatedAt}.
 * </p>
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
}
