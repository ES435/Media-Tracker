package app.mediatracker.feature.auth.service;

import app.mediatracker.feature.auth.exception.InvalidPasswordException;
import app.mediatracker.feature.auth.exception.PasswordsDontMatchException;
import app.mediatracker.feature.auth.exception.UsernameAlreadyExistsException;
import app.mediatracker.feature.user.exception.UserNotFoundException;
import app.mediatracker.feature.user.model.User;
import app.mediatracker.feature.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Application logic for authentication (login, registration).
 * <p>
 * This service is exclusively responsible for creating and verifying users.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Verifies a user's login credentials and returns the corresponding User object
     * upon successful authentication.
     *
     * @param username the provided username
     * @param password the provided password
     * @return the User object
     * @throws UserNotFoundException    if no user with the corresponding username exists
     * @throws InvalidPasswordException if the password does not match
     */
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidPasswordException(username);
        }

        return user;
    }

    /**
     * Registers a new user in the database. The username must be unique.
     * The password is hashed before storage.
     *
     * @param username    the username of the new user
     * @param password    the password of the new user
     * @param passwordRep the repeated password for confirmation
     * @throws UsernameAlreadyExistsException if the username already exists
     * @throws PasswordsDontMatchException    if the password and confirmation do not match
     */
    public void register(String username, String password, String passwordRep) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("Username " + username + " already exists.");
        }
        if (!password.equals(passwordRep)) {
            throw new PasswordsDontMatchException("Passwords don't match.");
        }
        String hashedPassword = passwordEncoder.encode(password);
        User user = createNewUser(username, hashedPassword);

        userRepository.insert(user);
    }

    /**
     * Helper method for creating a new User instance.
     * Sets default values (e.g., publicList = true).
     */
    private User createNewUser(String username, String passwordHash) {
        return User.builder()
                .username(username)
                .passwordHash(passwordHash)
                .publicList(true)
                .build();
    }
}