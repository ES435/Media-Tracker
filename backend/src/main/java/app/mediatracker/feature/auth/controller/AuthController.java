package app.mediatracker.feature.auth.controller;

import app.mediatracker.feature.auth.dto.LoginRequest;
import app.mediatracker.feature.auth.dto.LoginResponse;
import app.mediatracker.feature.auth.dto.RegistrationRequest;
import app.mediatracker.feature.auth.model.RefreshToken;
import app.mediatracker.feature.auth.service.AuthService;
import app.mediatracker.feature.auth.service.RefreshTokenService;
import app.mediatracker.feature.auth.service.TokenService;
import app.mediatracker.feature.user.model.User;
import app.mediatracker.feature.user.repo.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final int accessExpirationSeconds;
    private final int refreshExpirationSeconds;

    public AuthController(AuthService authService, TokenService tokenService, RefreshTokenService refreshTokenService, UserRepository userRepository, @Value("${app.jwt.access-token-expiration-in-seconds}") int accessExpirationSeconds, @Value("${app.jwt.refresh-token-expiration-in-seconds}") int refreshExpirationSeconds) {
        this.authService = authService;
        this.tokenService = tokenService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.accessExpirationSeconds = accessExpirationSeconds;
        this.refreshExpirationSeconds = refreshExpirationSeconds;
    }

    /**
     * Authenticates the user based on the provided login request.
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {

            User user = authService.login(loginRequest.getUsername(), loginRequest.getPassword());

            String accessToken = tokenService.generateAccessToken(user.getId(), user.getUsername());
            String refreshToken = refreshTokenService.createAndStore(user.getId());

            int refreshMaxAge = loginRequest.getRememberMe() ? refreshExpirationSeconds : -1;

            response.addCookie(createCookie("accessToken", accessToken, accessExpirationSeconds));
            response.addCookie(createCookie("refreshToken", refreshToken, refreshMaxAge));

            response.addCookie(createMetadataCookie(user, loginRequest.getRememberMe()));

            return LoginResponse.builder()
                .username(user.getUsername())
                .profilePictureUrl(user.getProfilePictureUrl())
                .publicList(user.getPublicList())
                .build();
    }

    /**
     * Handles user registration.
     */
    @PostMapping("/register")
    public String register(@RequestBody RegistrationRequest request) {

        authService.register(request.getUsername(), request.getPassword(), request.getPasswordRep());
        return "User registered";
    }

    @PostMapping("/refresh")
    public String refresh(@CookieValue("refreshToken") String refreshTokenCookie, HttpServletResponse response) {
         Optional<RefreshToken> oldToken = refreshTokenService.findByToken(refreshTokenCookie);
        if(oldToken.isEmpty()) {
            response.addCookie(createCookie("accessToken", null, 0));
            response.addCookie(createCookie("refreshToken", null, 0));
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        User user = userRepository.findById(oldToken.get().getUserId()).get();

        String accessToken = tokenService.generateAccessToken(user.getId(), user.getUsername());
        String refreshToken = refreshTokenService.createAndStore(user.getId());

        response.addCookie(createCookie("accessToken", accessToken, accessExpirationSeconds));
        response.addCookie(createCookie("refreshToken", refreshToken, refreshExpirationSeconds));

        return "Refresh successful";
    }

    @PostMapping("/logout")
    public String logout(@CookieValue("refreshToken") String refreshTokenCookie, HttpServletResponse response) {
        Optional<RefreshToken> oldToken = refreshTokenService.findByToken(refreshTokenCookie);
        if(oldToken.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }
        refreshTokenService.deleteToken(oldToken.get());

        Cookie access = createCookie("accessToken", "", 0);
        Cookie refresh = createCookie("refreshToken", "", 0);

        response.addCookie(access);
        response.addCookie(refresh);
        return "Logout successful";
    }

    private Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);

        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "Lax");
        return cookie;
    }

private Cookie createMetadataCookie(User user, boolean rememberMe) {

    String metadata = String.format("{\"username\":\"%s\",\"profilePictureUrl\":\"%s\",\"userid\":\"%s\"}",
            user.getUsername(),
            user.getProfilePictureUrl() != null ? user.getProfilePictureUrl() : "",
            user.getId().toHexString());

    Cookie cookie = new Cookie("user_metadata", URLEncoder.encode(metadata, StandardCharsets.UTF_8));
    cookie.setHttpOnly(false);
    cookie.setSecure(false);
    cookie.setPath("/");
    
    if (rememberMe) {
        cookie.setMaxAge(refreshExpirationSeconds);
    } else {
        cookie.setMaxAge(-1);
    }
    
    return cookie;
}
}