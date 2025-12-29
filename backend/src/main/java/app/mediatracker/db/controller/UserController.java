package app.mediatracker.db.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.mediatracker.db.service.UserService;
import lombok.RequiredArgsConstructor;


/**
 * REST-Controller für die Suche von Usern und das Anzeigen derer Account-Pages.
 * <p>
 * Stellt Endpunkte zum Suchen und Anzeigen von existierenden Usern bereit.
 * Das Frontend greift über diese Endpunkte zu.
 * </p>
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService UserService;
}
