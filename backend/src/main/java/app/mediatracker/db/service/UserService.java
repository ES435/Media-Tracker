import java.util.List;
import java.util.Set;

import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import app.mediatracker.db.api.UserPageResponse;
import app.mediatracker.db.api.UserSearchResponse;
import app.mediatracker.db.api.UserSummary;
import app.mediatracker.db.domain.User;
import app.mediatracker.db.domain.UserLibraryEntry;
import app.mediatracker.db.repo.UserLibraryEntryRepository;
import app.mediatracker.db.repo.UserRepository;
import app.mediatracker.search.core.dto.SearchResult;
import lombok.RequiredArgsConstructor;

/**
 * Anwendungslogik für die Medienbibliothek.
 * <p>
 * Diese Service-Klasse koordiniert die Speicherung und Abfrage von Bibliothekseinträgen eines Users.
 * In dieser Variante werden Medien-Basisdaten direkt im Eintrag als Snapshot gespeichert (kein separates MediaItem).
 * </p>
 * <p>
 * Persistenz: MongoDB über Spring Data Repositories. Zeitstempel werden durch Mongo Auditing gesetzt.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserService{

    private final UserRepository userRepository;

    /**
     * Sucht über alle passenden Provider und kombiniert die Ergebnisse.
     *
     * Verhalten:
     * - Wenn {@code types} leer oder {@code null} ist, werden alle Provider verwendet.
     * - Pro Provider wird mit {@code limitPerType} begrenzt.
     * - Duplikate werden anhand von {@code type#id} entfernt (stabile Einfüge-Reihenfolge).
     */
    @Override
    private UserSearchResponse searchUser(String partName, int limit) {
        List<User> searchResults = userRepository.findByNameContainingIgnoreCase(partName);
        return searchResults.stream().map(this::toResponse).toList();
    }
    

    private UserPageResponse getUserPage(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserPage'");
    return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    private UserSummary toResponse(User user) {
        return UserSummary.builder()
            .name(user.getUsername())
            .profilePictureUrl(user.getProfilePictureUrl())
            .publicList(user.getPublicList())
            .build();
    }
}