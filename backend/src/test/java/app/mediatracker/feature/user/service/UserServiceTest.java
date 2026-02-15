package app.mediatracker.feature.user.service;

import app.mediatracker.feature.library.repo.UserLibraryEntryRepository;
import app.mediatracker.feature.user.dto.UserPageResponse;
import app.mediatracker.feature.user.model.User;
import app.mediatracker.feature.user.repo.UserRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 * Focuses on user profile retrieval and visibility permissions.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserLibraryEntryRepository libraryRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserPage_shouldThrowException_whenUserNotFound() {
        // Arrange: Simulate user not existing in DB
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act & Assert: Should result in a 404 response status
        assertThrows(org.springframework.web.server.ResponseStatusException.class, () ->
                userService.getUserPage("unknown", null)
        );
    }

    @Test
    void getUserPage_shouldReturnEmptyList_whenProfileIsPrivateAndCallerIsStranger() {
        // Arrange: Target user has a private list
        String targetName = "shyUser";
        User targetUser = User.builder().id(new ObjectId()).username(targetName).publicList(false).build();

        when(userRepository.findByUsername(targetName)).thenReturn(Optional.of(targetUser));

        // Act: Principal is null (anonymous visitor/stranger)
        UserPageResponse response = userService.getUserPage(targetName, null);

        // Assert: Verify privacy logic—list must be empty and DB should not be queried
        assertTrue(response.getUserMediaList().isEmpty(), "Private list should be empty for strangers");
        verify(libraryRepository, never()).findByUserId(any());
    }

    @Test
    void getUserPage_shouldReturnList_whenProfileIsPrivateButCallerIsOwner() {
        // Arrange: Private user profile
        String myName = "me";
        ObjectId myId = new ObjectId();
        User me = User.builder().id(myId).username(myName).publicList(false).build();

        when(userRepository.findByUsername(myName)).thenReturn(Optional.of(me));
        when(userRepository.findById(myId)).thenReturn(Optional.of(me));

        // Simulate a logged-in user matching the target profile
        Principal principal = () -> myId.toString();

        // Act
        userService.getUserPage(myName, principal);

        // Assert: Since the caller is the owner, library access is authorized
        verify(libraryRepository).findByUserId(myId);
    }
}