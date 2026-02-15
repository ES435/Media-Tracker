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

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock UserLibraryEntryRepository libraryRepository;
    @InjectMocks UserService userService;

    @Test
    void getUserPage_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(org.springframework.web.server.ResponseStatusException.class, () ->
                userService.getUserPage("unknown", null)
        );
    }

    @Test
    void getUserPage_shouldReturnEmptyList_whenProfileIsPrivateAndCallerIsStranger() {
        String targetName = "shyUser";
        User targetUser = User.builder().id(new ObjectId()).username(targetName).publicList(false).build();

        when(userRepository.findByUsername(targetName)).thenReturn(Optional.of(targetUser));

        // Act: Principal is null (not logged in)
        UserPageResponse response = userService.getUserPage(targetName, null);

        // Assert: List should be empty
        assertTrue(response.getUserMediaList().isEmpty(), "Private list should be empty for strangers");
        // Ensure DB was not queried for library entries
        verify(libraryRepository, never()).findByUserId(any());
    }

    @Test
    void getUserPage_shouldReturnList_whenProfileIsPrivateButCallerIsOwner() {
        String myName = "me";
        ObjectId myId = new ObjectId();
        User me = User.builder().id(myId).username(myName).publicList(false).build();

        when(userRepository.findByUsername(myName)).thenReturn(Optional.of(me));
        when(userRepository.findById(myId)).thenReturn(Optional.of(me));

        // Mock Principal (simulate logged-in user)
        Principal principal = () -> myId.toString();

        userService.getUserPage(myName, principal);

        // Assert: Library access is allowed
        verify(libraryRepository).findByUserId(myId);
    }
}