package app.mediatracker.db.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.mediatracker.db.api.LibraryEntryResponse;
import app.mediatracker.db.api.UserPageResponse;
import app.mediatracker.db.api.UserSearchResponse;
import app.mediatracker.db.api.UserSummary;
import app.mediatracker.db.service.UserService;
import app.mediatracker.search.core.dto.SearchResult;
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
        @PathVariable("username") String username
    ) {

        return ResponseEntity.ok(userService.getUserPage(username));
    }
    
}
