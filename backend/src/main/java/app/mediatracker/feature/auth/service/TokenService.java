package app.mediatracker.feature.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.bson.codecs.ObjectIdGenerator;
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

    public TokenService(@Value("${app.jwt.secret}") String secretKey, @Value("${app.jwt.access-token-expiration-in-millis}") int accessTokenExpirationInMillis, @Value("${app.jwt.refresh-token-expiration-in-millis}") int refreshTokenExpirationInMillis) {
        this.algorithm = Algorithm.HMAC256(secretKey);
        this.verifier = JWT.require(algorithm).build();
        this.accessTokenExpirationInMillis = accessTokenExpirationInMillis;
        this.refreshTokenExpirationInMillis = refreshTokenExpirationInMillis;
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
     * @return true, wenn Token gültig für den Benutzer ist
     */
    public String verifyTokenAndGetUserId(String token) {
        return JWT.require(algorithm)
                .build()
                .verify(token)
                .getSubject();
    }

    /**
     * liest den username aus einem Token aus
     * @param token JWT-Token
     * @return username
     */
    public String extractUsername(String token) {
        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }
}
