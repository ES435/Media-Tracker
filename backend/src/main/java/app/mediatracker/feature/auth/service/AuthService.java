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
 * Anwendungslogik für die Authentifizierung (Login, Registrierung).
 * <p>
 * Dieser Service kümmert sich ausschließlich um das Erstellen und Verifizieren von Benutzern.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Prüft die Login-Daten eines Benutzers und gibt das entsprechende User-Objekt zurück,
     * wenn die Authentifizierung erfolgreich war.
     *
     * @param username der eingegebene Username
     * @param password das eingegebene Passwort
     * @return das User Objekt
     * @throws UserNotFoundException    falls kein User mit entsprechendem Username existiert
     * @throws InvalidPasswordException falls das Passwort nicht übereinstimmt
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
     * Registriert einen neuen Benutzer in der Datenbank. Der Benutzername muss eindeutig sein.
     * Das Passwort wird vor der Speicherung gehasht.
     *
     * @param username der Benutzername des neuen Benutzers.
     * @param password das Passwort des neuen Benutzers.
     * @throws UsernameAlreadyExistsException wenn der Benutzername bereits existiert.
     */
    public void register(String username, String password, String passworRep) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("Username " + username + " already exists.");
        }
        if (!password.equals(passworRep)) {
            throw new PasswordsDontMatchException("Passwords don't match.");
        }
        String hashedPassword = passwordEncoder.encode(password);
        User user = createNewUser(username, hashedPassword);

        userRepository.insert(user);
    }

    /**
     * Hilfsmethode zum Erstellen einer neuen User-Instanz.
     * Setzt Standardwerte (z.B. publicList = true).
     */
    private User createNewUser(String username, String passwordHash) {
        return User.builder()
                .username(username)
                .passwordHash(passwordHash)
                .publicList(true)
                .build();
    }
}