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
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * REST controller for authentication endpoints.
 * Provides login, registration, logout, and access/refresh token management.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final int accessExpirationSeconds;
    private final int refreshExpirationSeconds;

    /**
     * Constructor for AuthController.
     *
     * @param authService service for authentication logic
     * @param tokenService service for generating JWTs
     * @param refreshTokenService service for managing refresh tokens in the database
     * @param userRepository repository for accessing user data
     * @param accessExpirationSeconds expiration time for access tokens in seconds
     * @param refreshExpirationSeconds expiration time for refresh tokens in seconds
     */
    public AuthController(AuthService authService, TokenService tokenService, RefreshTokenService refreshTokenService, UserRepository userRepository, @Value("${app.jwt.access-token-expiration-in-seconds}") int accessExpirationSeconds, @Value("${app.jwt.refresh-token-expiration-in-seconds}") int refreshExpirationSeconds) {
        this.authService = authService;
        this.tokenService = tokenService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.accessExpirationSeconds = accessExpirationSeconds;
        this.refreshExpirationSeconds = refreshExpirationSeconds;
    }

    /**
     * Handles user login: authenticates the user, generates access and refresh tokens,
     * and attaches them as cookies to the HTTP response.
     *
     * @param loginRequest the login payload containing username, password and optional remember-me flag
     * @param response the HTTP response to which cookies will be added
     * @return a ResponseEntity with a success message upon successful login
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {

            User user = authService.login(loginRequest.getUsername(), loginRequest.getPassword());

            String accessToken = tokenService.generateAccessToken(user.getId(), user.getUsername());
            String refreshToken = refreshTokenService.createAndStore(user.getId());

            int refreshMaxAge = loginRequest.getRememberMe() ? refreshExpirationSeconds : -1;

            response.addCookie(createCookie("accessToken", accessToken, accessExpirationSeconds));
            response.addCookie(createCookie("refreshToken", refreshToken, refreshMaxAge));


            return ResponseEntity.ok("Login successful.");
    }

    /**
     * Processes registration requests for new users.
     *
     * @param request the registration payload with username, password, and password confirmation
     * @return a ResponseEntity with a success message if registration was successful
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationRequest request) {

        authService.register(request.getUsername(), request.getPassword(), request.getPasswordRep());
        return ResponseEntity.ok("Registration successful.");
    }

    /**
     * Refreshes the access and refresh tokens for a user.
     * Validates the provided refresh token from the cookie; if valid, issues new tokens and
     * updates the cookies on the response. If invalid, existing cookies are cleared.
     *
     * @param refreshTokenCookie the token extracted from the "refreshToken" cookie
     * @param response the HttpServletResponse used to update the cookies
     * @return a ResponseEntity with a success message upon successful refresh
     * @throws ResponseStatusException if the refresh token is invalid or not found
     */
    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@CookieValue("refreshToken") String refreshTokenCookie, HttpServletResponse response) {
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

        return ResponseEntity.ok("Refresh successful");
    }

    /**
     * Logs the user out. Deletes the refresh token from the database and invalidates the
     * corresponding cookies on the client (browser).
     *
     * @param refreshTokenCookie the current refresh token from the cookie
     * @param response the HttpServletResponse used to clear the cookies
     * @return a ResponseEntity with a success message
     * @throws ResponseStatusException if the refresh token is invalid
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue("refreshToken") String refreshTokenCookie, HttpServletResponse response) {
        Optional<RefreshToken> oldToken = refreshTokenService.findByToken(refreshTokenCookie);
        if(oldToken.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }
        refreshTokenService.deleteToken(oldToken.get());

        Cookie access = createCookie("accessToken", "", 0);
        Cookie refresh = createCookie("refreshToken", "", 0);

        response.addCookie(access);
        response.addCookie(refresh);
        return ResponseEntity.ok("Logout successful");
    }

    /**
     * Handles the /me endpoint to retrieve user information based on the access token.
     *
     * @param accessToken the token extracted from the "accessToken" cookie
     * @return a {@link LoginResponse} with username, profile image URL, and public-list flag
     * @throws ResponseStatusException if the token is invalid or the user cannot be found
     */
    @GetMapping("/me")
    public LoginResponse me(@CookieValue("accessToken")String accessToken) {
        if(accessToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid access token");
        }
        ObjectId userID = new ObjectId(tokenService.verifyTokenAndGetUserId(accessToken));
        User user = userRepository.findById(userID).isPresent() ? userRepository.findById(userID).get() : null;

        if(user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid access token");
        }

        return LoginResponse.builder()
                .username(user.getUsername())
                .profilePictureUrl(user.getProfilePictureUrl())
                .publicList(user.getPublicList())
                .build();
    }

    /**
     * Creates a new HTTP cookie with specific attributes.
     * The cookie is marked HttpOnly, uses SameSite=Lax, path "/", and Secure=false.
     *
     * @param name the cookie name
     * @param value the cookie value
     * @param maxAge max age in seconds; values < 0 create a session cookie (not persisted)
     * @return the newly created {@code Cookie}
     */
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