package app.mediatracker.feature.user.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import app.mediatracker.feature.library.dto.LibraryEntryResponse;
import app.mediatracker.feature.library.dto.MediaItemSummary;
import app.mediatracker.feature.user.dto.UserPageResponse;
import app.mediatracker.feature.user.dto.UserSearchResponse;
import app.mediatracker.feature.user.dto.UserSummary;
import app.mediatracker.feature.user.model.User;
import app.mediatracker.feature.library.model.UserLibraryEntry;
import app.mediatracker.feature.library.repo.UserLibraryEntryRepository;
import app.mediatracker.feature.auth.exception.UsernameAlreadyExists;
import app.mediatracker.feature.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import app.mediatracker.feature.auth.exception.InvalidPasswordException;
import app.mediatracker.feature.user.exception.UserNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Anwendungslogik für die Userbibliothek.
 * <p>
 * Diese Service-Klasse koordiniert die Abfrage von Suchanfragen zu einem User.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
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
        List<User> searchResults = userRepository.findByUsernameContainingIgnoreCase(partName);
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
        User user = userRepository.findByUsername(username)
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

    /**
     * Prüft die Login-Daten eines Benutzers und gibt das entsprechende User-Objekt zurück,
     * wenn die Authentifizierung erfolgreich war.
     *
     * @param username der eingegebene Username
     * @param password das eingegebene Passwort
     * @return das User Objekt
     * @throws UserNotFoundException falls kein User mit entsprechendem Username existiert
     * @throws InvalidPasswordException falls das Passwort nicht übereinstimmt
     */
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if(!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidPasswordException(username);
        }

        return user;
    }

    /**
     * Registriert einen neuen Benutzer in der Datenbank. Der Benutzername muss eindeutig sein.
     * Das Passwort wird vor der Speicherung gehasht.
     *
     * @param username der Benutzername des neuen Benutzers.
     * @param password das Passwort des neuen Benutzers.
     * @throws UsernameAlreadyExists wenn der Benutzername bereits existiert.
     */
    public void register(String username, String password) {
        if(userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExists("Username " + username + " already exists.");
        }
        String hashedPassword = passwordEncoder.encode(password);
        User user = createNewUser(username, hashedPassword);

        userRepository.insert(user);
    }

    private User createNewUser(String username, String passwordHash) {
        return User.builder()
                .username(username)
                .passwordHash(passwordHash)
                .publicList(false)
                .build();
    }

}