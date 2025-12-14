package app.mediatracker.db.service;

import app.mediatracker.db.domain.User;
import app.mediatracker.db.domain.exception.UsernameAlreadyExists;
import app.mediatracker.db.repo.UserRepository;
import app.mediatracker.db.domain.exception.InvalidPasswordException;
import app.mediatracker.db.domain.exception.UserNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserService {

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

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

    public void register(String username, String password) {
        if(userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExists("Username " + username + " already exists.");
        }
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(null, username, hashedPassword, Instant.now(), Instant.now());

        userRepository.insert(user);
    }
}
