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

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock TokenService tokenService;
    @InjectMocks RefreshTokenService refreshTokenService;

    @Test
    void createAndStore_shouldDeleteOldAndSaveNew() {
        ObjectId userId = new ObjectId();
        String newToken = "new.refresh.token";

        when(tokenService.generateRefreshToken(userId)).thenReturn(newToken);

        String result = refreshTokenService.createAndStore(userId);

        assertEquals(newToken, result);
        // Important: Old tokens must be deleted for this user
        verify(refreshTokenRepository).deleteAllByUserId(userId);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }
}