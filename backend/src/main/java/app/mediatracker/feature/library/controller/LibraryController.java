package app.mediatracker.feature.library.controller;

import app.mediatracker.feature.library.dto.AddLibraryEntryRequest;
import app.mediatracker.feature.library.dto.LibraryEntryResponse;
import app.mediatracker.feature.library.dto.ManualEntryRequest;
import app.mediatracker.feature.library.model.LibraryEntryStatus;
import app.mediatracker.feature.library.service.LibraryService;
import app.mediatracker.feature.library.service.command.ManualEntryCommand;
import app.mediatracker.feature.search.core.dto.SearchResult;
import app.mediatracker.feature.user.model.User;
import app.mediatracker.feature.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * REST controller for the personal media library.
 * <p>
 * Exposes endpoints to read, create/update, and delete library entries.
 * </p>
 */
@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {

    private final UserService userService;
    private final LibraryService libraryService;

    /**
     * Returns the complete library of the currently authenticated user.
     *
     * @return 200 OK with all entries of the user in display format
     */
    @GetMapping
    public ResponseEntity<List<LibraryEntryResponse>> getLibrary(Principal principal) {
        User user = getCurrentUser(principal);
        List<LibraryEntryResponse> entries = libraryService.getLibraryForUser(user.getId());
        return ResponseEntity.ok(entries);
    }

    /**
     * Paginated view of the library, sorted by updatedAt DESC by default.
     */
    @GetMapping("/page")
    public ResponseEntity<Page<LibraryEntryResponse>> getLibraryPage(
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
            Principal principal
    ) {
        User user = getCurrentUser(principal);

        Page<LibraryEntryResponse> page = libraryService.getLibraryForUser(user.getId(), pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Creates a new library entry or updates an existing one (based on a SearchResult).
     *
     * @param request payload with status/rating/notes and the selected SearchResult
     * @return 200 OK with the saved/updated entry
     */
    @PostMapping()
    public ResponseEntity<LibraryEntryResponse> addOrUpdateEntry(@Valid @RequestBody AddLibraryEntryRequest request, Principal principal) {
        User user = getCurrentUser(principal);

        SearchResult searchResult = request.getSearchResult();
        LibraryEntryStatus status = request.getStatus();

        LibraryEntryResponse response = libraryService.addOrUpdateEntryFromSearchResult(
                user.getId(),
                searchResult,
                status,
                request.getRating(),
                request.getNotes()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a new library entry based on manual input data.
     * Uses the Clean Code Command Pattern.
     *
     * @param request payload with the manual data
     * @return 200 OK with the saved entry
     */
    @PostMapping("/manualEntry")
    public ResponseEntity<LibraryEntryResponse> addManualEntry(
            @Valid @RequestBody ManualEntryRequest request,
            Principal principal
    ) {
        User user = getCurrentUser(principal);

        ManualEntryCommand command = ManualEntryCommand.builder()
                .userId(user.getId())
                .type(request.getType())
                .title(request.getTitle())
                .author(request.getAuthor())
                .imageUrl(request.getImageUrl())
                .meta(request.getMeta())
                .status(request.getStatus())
                .rating(request.getRating())
                .notes(request.getNotes())
                .build();

        LibraryEntryResponse response = libraryService.addManualEntry(command);
        return ResponseEntity.ok(response);
    }

    /**
     * Allows the user to edit an existing manual entry.
     * Uses the Clean Code Command Pattern.
     *
     * @param request payload with the updated data
     * @param entryId entry identifier
     * @return 200 OK with the updated entry
     */
    @PatchMapping("/manualEntry/{entryId}")
    public ResponseEntity<LibraryEntryResponse> updateManualEntry(
            @RequestBody ManualEntryRequest request,
            @PathVariable("entryId") String entryId,
            Principal principal
    ) {
        User user = getCurrentUser(principal);

        ManualEntryCommand command = ManualEntryCommand.builder()
                .userId(user.getId())
                .type(request.getType())
                .title(request.getTitle())
                .author(request.getAuthor())
                .imageUrl(request.getImageUrl())
                .meta(request.getMeta())
                .status(request.getStatus())
                .rating(request.getRating())
                .notes(request.getNotes())
                .build();

        LibraryEntryResponse response = libraryService.updateManualEntry(entryId, command);
        return ResponseEntity.ok(response);
    }

    /**
     * Removes a library entry of the current user.
     *
     * @param entryId technical ID of the entry (MongoDB ID)
     * @return 204 No Content
     */
    @DeleteMapping("/{entryId}")
    public ResponseEntity<Void> removeEntry(
            @PathVariable("entryId") String entryId,
            Principal principal
    ) {
        User user = getCurrentUser(principal);

        libraryService.removeEntry(user.getId(), entryId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Helper to load the current user from the security context.
     */
    private User getCurrentUser(Principal principal) {
        return userService.getUserById(new ObjectId(principal.getName()));
    }
}