package app.mediatracker.feature.auth.controller;

import app.mediatracker.feature.auth.dto.LoginRequest;
import app.mediatracker.feature.auth.dto.RegistrationRequest;
import app.mediatracker.feature.auth.exception.InvalidPasswordException;
import app.mediatracker.feature.auth.model.RefreshToken;
import app.mediatracker.feature.auth.repo.RefreshTokenRepository;
import app.mediatracker.feature.auth.service.AuthService;
import app.mediatracker.feature.auth.service.RefreshTokenService;
import app.mediatracker.feature.auth.service.TokenService;
import app.mediatracker.feature.user.exception.UserNotFoundException;
import app.mediatracker.feature.user.model.User;
import app.mediatracker.feature.user.repo.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, TokenService tokenService, RefreshTokenService refreshTokenService, UserRepository userRepository) {
        this.authService = authService;
        this.tokenService = tokenService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
    }

    /**
     * Authenticates the user based on the provided login request.
     */
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        try {
            User user = authService.login(loginRequest.getUsername(), loginRequest.getPassword());

            String accessToken = tokenService.generateAccessToken(user.getId(), user.getUsername());
            String refreshToken = refreshTokenService.createAndStore(user.getId());

            int refreshMaxAge = loginRequest.getRememberMe() ? 60 * 60 * 24 * 14 : -1;

            response.addCookie(createCookie("accessToken", accessToken, 60 * 15)); //ToDo: ExpireDates iwo zentral konfigurieren?
            response.addCookie(createCookie("refreshToken", refreshToken, refreshMaxAge));

            return "Login successful.";
        } catch (InvalidPasswordException | UserNotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login failed. Invalid username or password.");
        }
    }

    /**
     * Handles user registration.
     */
    @PostMapping("/register")
    public String register(@RequestBody RegistrationRequest request) {

        authService.register(request.getUsername(), request.getPassword());
        return "User registered";
    }

    @PostMapping("/refresh")
    public String refresh(@CookieValue("refreshToken") String refreshTokenCookie, HttpServletResponse response) {
         Optional<RefreshToken> oldToken = refreshTokenService.findByToken(refreshTokenCookie);
        if(oldToken.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        User user = userRepository.findById(oldToken.get().getUserId()).get();

        String accessToken = tokenService.generateAccessToken(user.getId(), user.getUsername());
        String refreshToken = refreshTokenService.createAndStore(user.getId());

        response.addCookie(createCookie("accessToken", accessToken, 15 * 60));
        response.addCookie(createCookie("refreshToken", refreshToken, 60 * 60 * 24 * 14));

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
}