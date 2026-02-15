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
     * Erstellt einen neuen Refresh-Token für den angegebenen Benutzer, löscht alle zuvor gespeicherten
     * Refresh-Token des Benutzers und speichert den neuen Token im Repository.
     *
     * @param userId die eindeutige Kennung des Benutzers, für den der Refresh-Token generiert wird
     * @return der neu erstellte Refresh-Token als String
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
     * Ruft eine RefreshToken-Entität basierend auf dem bereitgestellten Token-String ab.
     *
     * @param token der Token-String, nach dem in der Datenquelle gesucht werden soll
     * @return ein Optional, das den entsprechenden RefreshToken enthält, falls gefunden, andernfalls ein leeres Optional
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Löscht einen gegebenen Refresh-Token aus dem Repository.
     *
     * @param refreshToken die zu löschende RefreshToken-Instanz
     */
    public void deleteToken(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }
}