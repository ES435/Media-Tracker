package app.mediatracker.db.controller;

import app.mediatracker.db.domain.User;
import app.mediatracker.db.dto.JwtResponse;
import app.mediatracker.db.dto.LoginRequest;
import app.mediatracker.db.service.JwtService;
import app.mediatracker.db.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {

        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());

        String token = jwtService.generateToken(user.getUsername());

        return ResponseEntity.ok(new JwtResponse(token));
    }
}

