package app.mediatracker.feature.auth.service;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for TokenService.
 * Verifies the generation, extraction, and validation of JSON Web Tokens (JWT).
 */
class TokenServiceTest {

    private TokenService tokenService;

    // Test constants for JWT configuration
    private static final String SECRET = "a_very_long_secret_key_for_testing_purposes_123456";
    private static final long ACCESS_EXP = 900;  // 15 minutes
    private static final long REFRESH_EXP = 1200; // 20 minutes
    private final ObjectId userId = new ObjectId();
    private static final String USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        // Manually injecting dependencies usually handled by @Value in the Spring context
        tokenService = new TokenService(SECRET, ACCESS_EXP, REFRESH_EXP);
    }

    @Test
    void generateAccessToken_shouldReturnValidJwt() {
        // Act: Generate a new token
        String token = tokenService.generateAccessToken(userId, USERNAME);

        // Assert: Ensure token is created and contains the correct username claim
        assertNotNull(token);
        assertFalse(token.isEmpty());

        String extractedUser = tokenService.extractUsername(token);
        assertEquals(USERNAME, extractedUser);
    }

    @Test
    void verifyTokenAndGetUserId_shouldReturnUserId() {
        // Arrange: Create a token with a specific userId
        String token = tokenService.generateAccessToken(userId, USERNAME);

        // Act: Verify the token and extract the subject (userId)
        String extractedId = tokenService.verifyTokenAndGetUserId(token);

        // Assert: Extracted ID must match the original ObjectId string
        assertEquals(userId.toString(), extractedId);
    }

    @Test
    void extractUsername_shouldReturnNullForInvalidToken() {
        // Arrange: A malformed token string
        String invalidToken = "this.is.not.a.token";

        // Act: Attempt to extract info from it
        String result = tokenService.extractUsername(invalidToken);

        // Assert: Should handle the error gracefully and return null
        assertNull(result);
    }
}