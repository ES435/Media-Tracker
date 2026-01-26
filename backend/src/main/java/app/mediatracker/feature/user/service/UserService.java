package app.mediatracker.feature.user.service;

import app.mediatracker.feature.library.dto.LibraryEntryResponse;
import app.mediatracker.feature.library.dto.MediaItemSummary;
import app.mediatracker.feature.library.model.UserLibraryEntry;
import app.mediatracker.feature.library.repo.UserLibraryEntryRepository;
import app.mediatracker.feature.user.dto.UserPageResponse;
import app.mediatracker.feature.user.dto.UserSearchResponse;
import app.mediatracker.feature.user.dto.UserSummary;
import app.mediatracker.feature.user.exception.UserNotFoundException;
import app.mediatracker.feature.user.model.User;
import app.mediatracker.feature.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

/**
 * Anwendungslogik für User-Profile und Suche.
 * <p>
 * Authentication-Logik wurde in den AuthService ausgelagert (Separation of Concerns).
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserLibraryEntryRepository userLibraryEntryRepository;
    // BCryptPasswordEncoder entfernt -> gehört hier nicht hin!

    /**
     * Führt eine User-Suche anhand von einem Teilstring aus.
     */
    public UserSearchResponse searchUser(String partName, int limit) {
        List<User> searchResults = userRepository.findByUsernameContainingIgnoreCase(partName);
        List<UserSummary> summaries = searchResults.stream()
                .map(this::toSummary)
                .limit(limit)
                .toList();
        return new UserSearchResponse(summaries);
    }

    /**
     * Gibt die notwendigen Daten für die User-Page eines Users aus.
     */
    public UserPageResponse getUserPage(String username, Principal principal) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + username + " not found" ));

        UserSummary userSummary = toSummary(user);

        boolean isOwner = false;
        if(principal != null) {
            User currentLoggedInUser = getUserById(new ObjectId(principal.getName()));
            isOwner = user.equals(currentLoggedInUser);
        }

        List<LibraryEntryResponse> userMediaList;
        if(user.getPublicList() || isOwner) {
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

    /**
     * Interne Methode zum Laden des vollen User-Objekts (z.B. für LibraryController).
     * @param username der eindeutige Username
     * @return das User Objekt
     * @throws UserNotFoundException wenn User nicht existiert
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    public User getUserById(ObjectId userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    // --- Private Helper ---

    private UserSummary toSummary(User user) {
        return UserSummary.builder()
                .username(user.getUsername())
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

    // HIER WURDEN login(), register() und createNewUser() ENTFERNT.
}