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
 * REST-Controller für Authentifizierungs-Endpunkte.
 * Bietet Funktionalitäten für Login, Registrierung, Logout sowie die Verwaltung von Access- und Refresh-Tokens.
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
     * Konstruktor für den AuthController.
     *
     * @param authService Service für die Authentifizierungslogik
     * @param tokenService Service zur Generierung von JWTs
     * @param refreshTokenService Service zur Verwaltung von Refresh-Tokens in der Datenbank
     * @param userRepository Repository für den Zugriff auf Benutzerdaten
     * @param accessExpirationSeconds Ablaufzeit für Access-Tokens in Sekunden
     * @param refreshExpirationSeconds Ablaufzeit für Refresh-Tokens in Sekunden
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
     * Verarbeitet den Login-Prozess eines Benutzers. Authentifiziert den Benutzer anhand der Anmeldedaten,
     * generiert Access- und Refresh-Tokens und fügt diese als Cookies der Antwort hinzu.
     *
     * @param loginRequest die Login-Anfrage mit Benutzernamen, Passwort und optionalem "Remember Me"-Flag
     * @param response die HTTP-Antwort, der die Cookies hinzugefügt werden
     * @return ein ResponseEntity-Objekt mit einer Erfolgsmeldung bei erfolgreichem Login
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {

            User user = authService.login(loginRequest.getUsername(), loginRequest.getPassword());

            String accessToken = tokenService.generateAccessToken(user.getId(), user.getUsername());
            String refreshToken = refreshTokenService.createAndStore(user.getId());

            int refreshMaxAge = loginRequest.getRememberMe() ? refreshExpirationSeconds : -1;

            response.addCookie(createCookie("accessToken", accessToken, accessExpirationSeconds));
            response.addCookie(createCookie("refreshToken", refreshToken, refreshMaxAge));


            return ResponseEntity.ok("Login erfolgreich.");
    }

    /**
     * Verarbeitet Registrierungsanfragen für neue Benutzer.
     *
     * @param request die Registrierungsanfrage mit Benutzername, Passwort und Passwortwiederholung
     * @return ein {@code ResponseEntity} mit einer Erfolgsmeldung, wenn die Registrierung erfolgreich war
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationRequest request) {

        authService.register(request.getUsername(), request.getPassword(), request.getPasswordRep());
        return ResponseEntity.ok("Registrierung erfolgreich.");
    }

    /**
     * Erneuert den Access- und Refresh-Token für einen Benutzer.
     * Die Methode prüft die Gültigkeit des übergebenen Refresh-Token-Cookies, generiert bei Gültigkeit
     * neue Tokens und aktualisiert die Cookies in der Antwort. Falls der Token ungültig ist,
     * werden bestehende Cookies gelöscht.
     *
     * @param refreshTokenCookie der aus dem "refreshToken"-Cookie extrahierte Token
     * @param response das HttpServletResponse-Objekt zum Aktualisieren der Cookies
     * @return ein ResponseEntity mit einer Erfolgsmeldung bei erfolgreicher Erneuerung
     * @throws ResponseStatusException wenn der Refresh-Token ungültig ist oder nicht gefunden wurde
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
     * Meldet den Benutzer ab. Löscht den Refresh-Token aus der Datenbank und entwertet die
     * entsprechenden Cookies im Client (Browser).
     *
     * @param refreshTokenCookie der aktuelle Refresh-Token aus dem Cookie
     * @param response das HttpServletResponse-Objekt zum Löschen der Cookies
     * @return eine ResponseEntity mit einer Erfolgsmeldung
     * @throws ResponseStatusException wenn der Refresh-Token ungültig ist
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
     * Verarbeitet den /me Endpunkt, um Benutzerinformationen basierend auf dem Access-Token abzurufen.
     *
     * @param accessToken der aus dem "accessToken"-Cookie extrahierte Token.
     * @return ein {@link LoginResponse} Objekt mit Benutzername, Profilbild-URL und Status der öffentlichen Liste.
     * @throws ResponseStatusException wenn der Token ungültig ist oder der Benutzer nicht gefunden wurde.
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
     * Erstellt ein neues HTTP-Cookie mit spezifischen Attributen.
     * Das Cookie wird als HttpOnly markiert, verwendet die SameSite-Policy "Lax",
     * setzt den Pfad auf "/" und setzt das Secure-Flag auf false.
     *
     * @param name der Name des Cookies
     * @param value der Wert des Cookies
     * @param maxAge die maximale Lebensdauer des Cookies in Sekunden; ein Wert kleiner als Null
     *               bedeutet, dass das Cookie nicht dauerhaft gespeichert wird (Session-Cookie).
     * @return die neu erstellte {@code Cookie} Instanz
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