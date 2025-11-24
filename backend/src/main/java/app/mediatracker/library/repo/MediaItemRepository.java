package app.mediatracker.library.repo;

import app.mediatracker.library.domain.MediaItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Spring Data Repository für die Speicherung und Abfrage von {@link app.mediatracker.library.domain.MediaItem}.
 * <p>
 * Bietet Standard-CRUD-Operationen über {@link MongoRepository} und eine maßgeschneiderte Lookup-Methode
 * zur eindeutigen Identifikation anhand von Medientyp und externer Provider-ID.
 * </p>
 */
public interface MediaItemRepository extends MongoRepository<MediaItem, String> {

    Optional<MediaItem> findByTypeAndExternalId(String type, String externalId);
}