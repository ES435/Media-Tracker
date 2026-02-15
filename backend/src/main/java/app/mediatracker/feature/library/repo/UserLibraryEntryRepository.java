package app.mediatracker.feature.library.repo;

import app.mediatracker.feature.library.model.UserLibraryEntry;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data Repository for {@link UserLibraryEntry}.
 * <p>
 * Provides CRUD operations and contains derived query methods to filter by user and
 * unique media item (defined via type + external ID).
 * </p>
 */
public interface UserLibraryEntryRepository extends MongoRepository<UserLibraryEntry, String> {

    List<UserLibraryEntry> findByUserId(ObjectId userId);

    Page<UserLibraryEntry> findByUserId(ObjectId userId, Pageable pageable);

    Optional<UserLibraryEntry> findByUserIdAndMediaTypeAndExternalId(ObjectId userId, String mediaType, String externalId);
}