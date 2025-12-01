package app.mediatracker.db.repo;

import app.mediatracker.db.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repository für Benutzer-Dokumente.
 * <p>
 * Verantwortlich für CRUD-Operationen auf der Collection <code>users</code>.
 * Hinweis: Der Benutzername ist per eindeutigem Index abgesichert (siehe Domain-Klasse <code>User</code>),
 * daher können Anmeldungen und Registrierungen zuverlässig über den Namen erfolgen.
 * </p>
 */
public interface UserRepository extends MongoRepository<User, String> {
    /**
     * Findet einen Benutzer anhand seines eindeutigen <code>name</code>.
     *
     * @param name der eindeutige Anzeigename (siehe Unique-Index auf <code>User.name</code>)
     * @return ein Optional mit dem Benutzer, falls vorhanden
     */
    Optional<User> findByName(String name);
}
