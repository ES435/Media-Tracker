package app.mediatracker.feature.user.repo;

import app.mediatracker.feature.user.model.User;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for user documents.
 * <p>
 * Responsible for CRUD operations on the <code>users</code> collection.
 * Note: The username is protected by a unique index (see domain class <code>User</code>),
 * therefore logins and registrations can reliably be performed via the name.
 * </p>
 */
public interface UserRepository extends MongoRepository<User, ObjectId> {

    /**
     * Finds a user by their unique <code>username</code>.
     *
     * @param username the unique display name (see unique index on <code>User.username</code>)
     * @return an Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds registered users based on a part of the username <code>namePart</code>.
     *
     * @param namePart input; part of the display name
     * @return a list of users matching the string, if any
     */
    List<User> findByUsernameContainingIgnoreCase(String namePart);

    /**
     * Checks if a user with the given username already exists.
     * * @param username the username to check
     * @return true if it exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Finds a user by their technical <code>userId</code>.
     * * @param userId the MongoDB ObjectId
     * @return an Optional containing the user if found
     */
    Optional<User> findUserById(ObjectId userId);
}