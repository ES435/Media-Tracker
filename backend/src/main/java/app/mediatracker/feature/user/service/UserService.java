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
 * Application logic for user profiles and search.
 * <p>
 * Authentication logic has been moved to the AuthService (Separation of Concerns).
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserLibraryEntryRepository userLibraryEntryRepository;
    // BCryptPasswordEncoder removed -> does not belong here!

    /**
     * Performs a user search based on a partial string.
     *
     * @param partName the string fragment to search for
     * @param limit    maximum number of results
     * @return UserSearchResponse containing the results
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
     * Retrieves the necessary data for a user's profile page.
     *
     * @param username  the username of the profile to retrieve
     * @param principal the currently authenticated user (optional)
     * @return UserPageResponse containing profile info and library entries (if visible)
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
        // Access logic: Show library if it is public or if the requester is the owner
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
     * Internal method to load the full user object (e.g., for LibraryController).
     * * @param username the unique username
     * @return the User object
     * @throws UserNotFoundException if user does not exist
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    /**
     * Retrieves a user by their technical ID.
     * * @param userId the MongoDB ObjectId
     * @return the User object
     * @throws UserNotFoundException if user does not exist
     */
    public User getUserById(ObjectId userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    // --- Private Helpers ---

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

    // NOTE: login(), register(), and createNewUser() were REMOVED from this service.
}