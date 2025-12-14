package app.mediatracker.db.controller;

import app.mediatracker.db.domain.User;
import app.mediatracker.db.dto.JwtResponse;
import app.mediatracker.db.dto.LoginRequest;
import app.mediatracker.db.service.JwtService;
import app.mediatracker.db.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

@RestController
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/auth/login")
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
}

