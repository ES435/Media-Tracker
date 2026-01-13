package app.mediatracker.db.controller;

import app.mediatracker.db.domain.User;
import app.mediatracker.db.dto.JwtResponse;
import app.mediatracker.db.dto.LoginRequest;
import app.mediatracker.db.dto.RegistrationRequest;
import app.mediatracker.db.service.JwtService;
import app.mediatracker.db.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * Authenticates the user based on the provided login request.
     * If successful, generates a JWT token, sets it as an HTTP-only cookie,
     * and returns a response with the token set in the headers.
     *
     * @param loginRequest the login request containing the username and password
     * @return a ResponseEntity with an HTTP-only JWT cookie in the response headers
     */
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest) {

        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());

        String token = jwtService.generateToken(user.getUsername());

        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Strict")
                .maxAge(60 * 60 * 72)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(SET_COOKIE, cookie.toString());

        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    /**
     * Handles user registration by accepting a registration request.
     * Calls the UserService to register the user with the provided username and password.
     *
     * @param request the registration request containing the username and password
     * @return a ResponseEntity indicating the success of the registration process
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        userService.register(request.getUsername(), request.getPassword());
        return ResponseEntity.ok("User registered");
    }
}

