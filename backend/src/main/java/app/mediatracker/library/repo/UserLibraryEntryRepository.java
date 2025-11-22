package app.mediatracker.library.repo;

import app.mediatracker.library.domain.UserLibraryEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserLibraryEntryRepository extends MongoRepository<UserLibraryEntry, String> {

    List<UserLibraryEntry> findByUserId(String userId);

    Optional<UserLibraryEntry> findByUserIdAndMediaItemId(String userId, String mediaItemId);
}