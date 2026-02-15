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
     * Performs a user search by partial name.
     *
     * Example: /api/user/search?partName=userName
     * @param partName the search term (e.g., "user_")
     * @param limit maximum number of results; default is 10
     * @return combined search results as JSON
     */
    @GetMapping("/search")
    public ResponseEntity<UserSearchResponse> searchUser(
            @RequestParam String partName,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        return ResponseEntity.ok(userService.searchUser(partName, limit));
    }


    /**
     * Returns the necessary data for a user's public profile page on request.
     *
     * Includes the username, profile picture, and the user's media list if it is set to public.
     * @param username the user's public username
     * @return 200 OK with the user's entries in display format
     */
    @GetMapping("/{username}")
    public ResponseEntity<UserPageResponse> getUserPage(
        @PathVariable("username") String username, Principal principal
    ) {

        return ResponseEntity.ok(userService.getUserPage(username, principal));
    }
    
}
