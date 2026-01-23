package app.mediatracker.feature.auth.service;

import app.mediatracker.feature.auth.model.RefreshToken;
import app.mediatracker.feature.auth.repo.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.sql.Ref;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;

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

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public void deleteToken(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }
}
