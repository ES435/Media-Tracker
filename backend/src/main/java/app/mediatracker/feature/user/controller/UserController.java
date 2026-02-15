package app.mediatracker.feature.user.controller;

import app.mediatracker.feature.user.dto.UserPageResponse;
import app.mediatracker.feature.user.dto.UserSearchResponse;
import app.mediatracker.feature.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


/**
 * REST controller for searching users and displaying their account pages.
 * <p>
 * Exposes endpoints to search for existing users and to fetch data for a user's public profile page.
 * </p>
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Führt eine User-Suche anhand von einem Teilstring aus.
     *
     * Beispiel: /api/user/search?partName=userName
     * @param partName     Suchbegriff (z. B. "user_")
     * @param limit Maximale Treffer pro Typ. Standard ist 10.
     * @return 
     * @return Liste kombinierter Suchergebnisse als JSON.
     */
    @GetMapping("/search")
    public ResponseEntity<UserSearchResponse> searchUser(
            @RequestParam String partName,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        return ResponseEntity.ok(userService.searchUser(partName, limit));
    }


    /**
     * Gibt auf Anfrage die notwendigen Daten für die User-Page eines Users aus.
     * 
     * Diese bestehen aus User-Namen, Profilbild und falls diese öffentlich gesetzt ist, der Medienliste des Users.
     * @param username oeffentlicher Name des Users.
     * @return 200 OK mit allen Einträgen des Users in Anzeigeform
     */
    @GetMapping("/{username}")
    public ResponseEntity<UserPageResponse> getUserPage(
        @PathVariable("username") String username, Principal principal
    ) {

        return ResponseEntity.ok(userService.getUserPage(username, principal));
    }
    
}
