package app.mediatracker.db.service;

import app.mediatracker.db.domain.User;
import app.mediatracker.db.repo.UserRepository;
import app.mediatracker.db.domain.exception.InvalidPasswordException;
import app.mediatracker.db.domain.exception.UserNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
}
