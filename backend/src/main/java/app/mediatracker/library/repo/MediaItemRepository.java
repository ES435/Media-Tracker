package app.mediatracker.library.repo;

import app.mediatracker.library.domain.MediaItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MediaItemRepository extends MongoRepository<MediaItem, String> {

    Optional<MediaItem> findByTypeAndExternalId(String type, String externalId);
}