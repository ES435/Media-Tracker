package app.mediatracker.db.repo;

import app.mediatracker.db.domain.UserLibraryEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data Repository für {@link app.mediatracker.db.domain.UserLibraryEntry}.
 * <p>
 * Stellt CRUD-Operationen bereit und enthält abgeleitete Query-Methoden zum Filtern nach User und
 * eindeutigem Medium (definiert über Typ + externe ID).
 * </p>
 */
public interface UserLibraryEntryRepository extends MongoRepository<UserLibraryEntry, String> {

    List<UserLibraryEntry> findByUserId(String userId);

    Page<UserLibraryEntry> findByUserId(String userId, Pageable pageable);

    Optional<UserLibraryEntry> findByUserIdAndMediaTypeAndExternalId(String userId, String mediaType, String externalId);
}