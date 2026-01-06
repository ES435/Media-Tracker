package app.mediatracker.db.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import app.mediatracker.db.api.LibraryEntryResponse;
import app.mediatracker.db.api.MediaItemSummary;
import app.mediatracker.db.api.UserPageResponse;
import app.mediatracker.db.api.UserSearchResponse;
import app.mediatracker.db.api.UserSummary;
import app.mediatracker.db.domain.User;
import app.mediatracker.db.domain.UserLibraryEntry;
import app.mediatracker.db.repo.UserLibraryEntryRepository;
import app.mediatracker.db.repo.UserRepository;
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
    private final UserLibraryEntryRepository userLibraryEntryRepository;

    /**
     * Führt eine User-Suche anhand von einem Teilstring aus.
     *
     * Beispiel: /api/user/search?partName=userName
     * @param partName     Suchbegriff (z. B. "user_")
     * @param limit Maximale Treffer pro Typ. Standard ist 10.
     * @return Liste kombinierter Suchergebnisse als JSON.
     */
    public UserSearchResponse searchUser(String partName, int limit) {
        List<User> searchResults = userRepository.findByNameContainingIgnoreCase(partName);
        List<UserSummary> summaries = searchResults.stream()
            .map(this::toSummary)
            .limit(limit)
            .toList();
        return new UserSearchResponse(summaries);
    }
    
    /**
    * Gibt die notwendigen Daten für die User-Page eines Users aus.
    * 
    * Diese bestehen aus User-Namen, Profilbild und – falls die Liste öffentlich ist – der Medienliste des Users.
    * @param username öffentlicher Name des Users
    * @return Einträge des Users in Anzeigeform (Profilbild, Name, Liste...)
    */
    public UserPageResponse getUserPage(String username) {
        User user = userRepository.findByName(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User" + username + "not found" ));
        UserSummary userSummary = toSummary(user);

        List<LibraryEntryResponse> userMediaList;
        if(user.getPublicList() == true) {
        List<UserLibraryEntry> userLibrary = userLibraryEntryRepository.findByUserId(user.getId());
        userMediaList = userLibrary.stream()
            .map(this::toLibraryEntryResponse)
            .toList();
        } else {
            userMediaList = List.of();
        }

        return UserPageResponse.builder()
            .user(userSummary)
            .userMediaList(userMediaList)
            .build();
    }


    private UserSummary toSummary(User user) {
        return UserSummary.builder()
            .username(user.getName())
            .profilePictureUrl(user.getProfilePictureUrl())
            .publicList(user.getPublicList())
            .build();
    }

    private LibraryEntryResponse toLibraryEntryResponse(UserLibraryEntry entry) {
        MediaItemSummary mediaSummary = MediaItemSummary.builder()
            .type(entry.getMediaType())
            .externalId(entry.getExternalId())
            .title(entry.getTitle())
            .imageUrl(entry.getImageUrl())
            .sourceUrl(entry.getSourceUrl())
            .build();
        return LibraryEntryResponse.builder()
            .id(entry.getId())
            .userId(entry.getUserId())
            .status(entry.getStatus())
            .rating(entry.getRating())
            .notes(entry.getNotes())
            .createdAt(entry.getCreatedAt())
            .updatedAt(entry.getUpdatedAt())
            .mediaItem(mediaSummary)
            .build();
    }
}