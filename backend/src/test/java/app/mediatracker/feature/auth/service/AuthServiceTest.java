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
        // Arrange
        User user = User.builder()
                .username("max")
                .passwordHash("hashed")
                .build();

        when(userRepository.findByUsername("max"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pw", "hashed"))
                .thenReturn(true);

        // Act
        User result = authService.login("max", "pw");

        // Assert
        assertEquals("max", result.getUsername());
    }

    @Test
    void login_userNotFound_shouldThrowException() {
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            authService.login("unknown", "pw");
        });
    }

    @Test
    void login_wrongPassword_shouldThrowInvalidPasswordException() {
        User user = User.builder()
                .username("max")
                .passwordHash("hashed")
                .build();

        when(userRepository.findByUsername("max"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed"))
                .thenReturn(false);

        assertThrows(InvalidPasswordException.class, () -> {
            authService.login("max", "wrong");
        });
    }

    @Test
    void register_newUsername_shouldSaveUser() {
        when(userRepository.existsByUsername("new"))
                .thenReturn(false);
        when(passwordEncoder.encode("pw"))
                .thenReturn("hashed");

        // Call with 3 parameters: username, password, passwordRepeat
        authService.register("new", "pw", "pw");

        verify(userRepository).insert(any(User.class));
    }

    @Test
    void register_existingUsername_shouldThrowException() {
        when(userRepository.existsByUsername("taken"))
                .thenReturn(true);

        // Call with 3 parameters
        assertThrows(UsernameAlreadyExistsException.class, () -> {
            authService.register("taken", "pw", "pw");
        });

        verify(userRepository, never()).insert(any(User.class));
    }
}