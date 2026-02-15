package app.mediatracker.feature.auth.service;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;
    // Test data
    private static final String SECRET = "a_very_long_secret_key_for_testing_purposes_123456";
    private static final long ACCESS_EXP = 900;
    private static final long REFRESH_EXP = 1200;
    private final ObjectId userId = new ObjectId();
    private static final String USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        // Simulate @Value injection via constructor
        tokenService = new TokenService(SECRET, ACCESS_EXP, REFRESH_EXP);
    }

    @Test
    void generateAccessToken_shouldReturnValidJwt() {
        String token = tokenService.generateAccessToken(userId, USERNAME);
        assertNotNull(token);
        assertFalse(token.isEmpty());

        String extractedUser = tokenService.extractUsername(token);
        assertEquals(USERNAME, extractedUser);
    }

    @Test
    void verifyTokenAndGetUserId_shouldReturnUserId() {
        String token = tokenService.generateAccessToken(userId, USERNAME);
        String extractedId = tokenService.verifyTokenAndGetUserId(token);

        assertEquals(userId.toString(), extractedId);
    }

    @Test
    void extractUsername_shouldReturnNullForInvalidToken() {
        String invalidToken = "this.is.not.a.token";
        String result = tokenService.extractUsername(invalidToken);
        assertNull(result);
    }
}