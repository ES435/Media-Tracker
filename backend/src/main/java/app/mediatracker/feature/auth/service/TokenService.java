package app.mediatracker.feature.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenService {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long accessTokenExpirationInMillis;
    private final long refreshTokenExpirationInMillis;

    public TokenService(@Value("${app.jwt.secret}") String secretKey,
                        @Value("${app.jwt.access-token-expiration-in-seconds}") long accessTokenExpirationInMillis,
                        @Value("${app.jwt.refresh-token-expiration-in-seconds}") long refreshTokenExpirationInMillis) {
        this.algorithm = Algorithm.HMAC256(secretKey);
        this.verifier = JWT.require(algorithm).build();
        this.accessTokenExpirationInMillis = accessTokenExpirationInMillis * 1000;
        this.refreshTokenExpirationInMillis = refreshTokenExpirationInMillis * 1000;
    }

    /**
     * Generiert einen AccessToken für den angegebenen username
     * @param username Benutzername
     * @param userId Besitzer des Tokens
     * @return JWT-Token als String
     */
    public String generateAccessToken(ObjectId userId, String username) {
        return JWT.create()
                .withSubject(userId.toString())
                .withClaim("username", username)
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpirationInMillis))
                .sign(algorithm);
    }

    /**
     * Generiert einen RefreshToken für die userId
     * @param userId Besitzer des Tokens
     * @return JWT-Token als String
     */
    public String generateRefreshToken(ObjectId userId) {
        return JWT.create()
                .withSubject(userId.toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpirationInMillis))
                .sign(algorithm);
    }


    /**
     * Validiert den Token und prüft, ober er für den angegebenen Benutzer gilt.
     *
     * @param token JWT-Token
     * @return User-ID (Subject) als String, wenn Token gültig ist
     */
    public String verifyTokenAndGetUserId(String token) {
        return JWT.require(algorithm)
                .build()
                .verify(token)
                .getSubject();
    }

    /**
     * Liest den username aus einem Token aus.
     * Korrigiert: Liest jetzt den Claim "username" statt dem Subject (ID).
     * * @param token JWT-Token
     * @return username oder null bei Fehler
     */
    public String extractUsername(String token) {
        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            // KORREKTUR: Wir lesen den expliziten Claim "username", nicht das Subject (das ist die ID)
            return decodedJWT.getClaim("username").asString();
        } catch (JWTVerificationException e) {
            return null;
        }
    }
}