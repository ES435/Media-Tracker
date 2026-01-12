package app.mediatracker.db.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET_KEY = "tempkey"; //ToDO: nicht hardcoded
    private static final long EXPIRATION_MS = 1000 * 60 * 60 * 72;

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtService() {
        this.algorithm = Algorithm.HMAC256(SECRET_KEY);
        this.verifier = JWT.require(algorithm).build();
    }

    /**
     * Generiert einen JWT für den angegebenen username
     * @param username Benutzername
     * @return JWT-Token als String
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + EXPIRATION_MS);

        return JWT.create()
                .withSubject(username)
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    /**
     * Validiert den Token und prüft, ober er für den angegebenen Benutzer gilt.
     *
     * @param token JWT-Token
     * @param username Benutzername
     * @return true, wenn Token gültig für den Benutzer ist
     */
    public boolean verifyToken(String token, String username) {
        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject().equals(username) && !isTokenExpired(decodedJWT);
        } catch (JWTVerificationException e) {
            return false;
        }
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

    /**
     * Prüft ob der JWT noch gültig ist.
     * @param decodedJWT
     * @return true = JWT gültig, false = JWT = expired
     */
    private boolean isTokenExpired(DecodedJWT decodedJWT) {
        return decodedJWT.getExpiresAt().before(new Date());
    }
}
