package app.mediatracker.feature.auth.service;

import app.mediatracker.feature.auth.model.RefreshToken;
import app.mediatracker.feature.auth.repo.RefreshTokenRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for RefreshTokenService.
 * Focuses on the lifecycle of refresh tokens, specifically the replacement of old tokens.
 */
@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void createAndStore_shouldDeleteOldAndSaveNew() {
        // Arrange
        ObjectId userId = new ObjectId();
        String newToken = "new.refresh.token";

        when(tokenService.generateRefreshToken(userId)).thenReturn(newToken);

        // Act
        String result = refreshTokenService.createAndStore(userId);

        // Assert
        assertEquals(newToken, result);

        // Important security aspect: verify that old tokens are deleted for this user
        // to prevent multiple active refresh sessions (Token Rotation).
        verify(refreshTokenRepository).deleteAllByUserId(userId);

        // Verify that the new token is persisted in the database
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }
}