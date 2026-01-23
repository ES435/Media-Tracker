package app.mediatracker.feature.auth.repo;

import app.mediatracker.feature.auth.model.RefreshToken;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserId(ObjectId userId);

    void deleteAllByUserId(ObjectId userId);
}
