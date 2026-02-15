package app.mediatracker.feature.auth.service;

import app.mediatracker.feature.auth.model.RefreshToken;
import app.mediatracker.feature.auth.repo.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;

    /**
     * Creates a new refresh token for the given user, deletes any previously stored
     * refresh tokens for this user, and persists the new token in the repository.
     *
     * @param userId the unique identifier of the user for whom the refresh token is generated
     * @return the newly created refresh token as a String
     */
    public String createAndStore(ObjectId userId) {

        String newToken = tokenService.generateRefreshToken(userId);

        refreshTokenRepository.deleteAllByUserId(userId);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .token(newToken)
                .build();

        refreshTokenRepository.save(refreshToken);

        return newToken;
    }

    /**
     * Retrieves a RefreshToken entity based on the provided token string.
     *
     * @param token the token string to look up in the data source
     * @return an Optional containing the corresponding RefreshToken if found; otherwise empty
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Deletes the given refresh token from the repository.
     *
     * @param refreshToken the RefreshToken instance to delete
     */
    public void deleteToken(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }
}