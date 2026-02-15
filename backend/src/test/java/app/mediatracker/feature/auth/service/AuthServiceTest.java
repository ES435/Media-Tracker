package app.mediatracker.feature.auth.service;

import app.mediatracker.feature.auth.exception.InvalidPasswordException;
import app.mediatracker.feature.auth.exception.UsernameAlreadyExistsException;
import app.mediatracker.feature.user.exception.UserNotFoundException;
import app.mediatracker.feature.user.model.User;
import app.mediatracker.feature.user.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for AuthService using Mockito.
 * Tests business logic for login and registration in isolation.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_validCredentials_shouldReturnUser() {
        // Arrange: Prepare a user with a hashed password in the mock repository
        User user = User.builder()
                .username("max")
                .passwordHash("hashed")
                .build();

        when(userRepository.findByUsername("max"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pw", "hashed"))
                .thenReturn(true);

        // Act: Perform the login
        User result = authService.login("max", "pw");

        // Assert: Ensure the correct user is returned
        assertEquals("max", result.getUsername());
    }

    @Test
    void login_userNotFound_shouldThrowException() {
        // Arrange: Simulate user not found in the database
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        // Act & Assert: Verify the custom exception is thrown
        assertThrows(UserNotFoundException.class, () -> {
            authService.login("unknown", "pw");
        });
    }

    @Test
    void login_wrongPassword_shouldThrowInvalidPasswordException() {
        // Arrange: User exists but provides the wrong password
        User user = User.builder()
                .username("max")
                .passwordHash("hashed")
                .build();

        when(userRepository.findByUsername("max"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed"))
                .thenReturn(false);

        // Act & Assert: Verify password validation failure
        assertThrows(InvalidPasswordException.class, () -> {
            authService.login("max", "wrong");
        });
    }

    @Test
    void register_newUsername_shouldSaveUser() {
        // Arrange: Setup registration conditions
        when(userRepository.existsByUsername("new"))
                .thenReturn(false);
        when(passwordEncoder.encode("pw"))
                .thenReturn("hashed");

        // Act: Call with 3 parameters: username, password, passwordRepeat
        authService.register("new", "pw", "pw");

        // Assert: Verify that the user was actually inserted into the DB
        verify(userRepository).insert(any(User.class));
    }

    @Test
    void register_existingUsername_shouldThrowException() {
        // Arrange: Simulate an already taken username
        when(userRepository.existsByUsername("taken"))
                .thenReturn(true);

        // Act & Assert: Call with 3 parameters and verify conflict exception
        assertThrows(UsernameAlreadyExistsException.class, () -> {
            authService.register("taken", "pw", "pw");
        });

        // Verify that no database insert was attempted
        verify(userRepository, never()).insert(any(User.class));
    }
}