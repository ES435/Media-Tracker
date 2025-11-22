package app.mediatracker.library.controller;

import app.mediatracker.library.api.AddLibraryEntryRequest;
import app.mediatracker.library.api.LibraryEntryResponse;
import app.mediatracker.library.domain.LibraryEntryStatus;
import app.mediatracker.library.service.LibraryService;
import app.mediatracker.core.dto.SearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {

    private static final String DEMO_USER_ID = "user_x";

    private final LibraryService libraryService;

    @GetMapping
    public ResponseEntity<List<LibraryEntryResponse>> getLibrary() {
        List<LibraryEntryResponse> entries = libraryService.getLibraryForUser(DEMO_USER_ID);
        return ResponseEntity.ok(entries);
    }

    @PostMapping
    public ResponseEntity<LibraryEntryResponse> addOrUpdateEntry(
            @RequestBody AddLibraryEntryRequest request
    ) {
        SearchResult searchResult = request.getSearchResult();
        LibraryEntryStatus status = request.getStatus();

        LibraryEntryResponse response = libraryService.addOrUpdateEntryFromSearchResult(
                DEMO_USER_ID,
                searchResult,
                status,
                request.getRating(),
                request.getNotes()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{entryId}")
    public ResponseEntity<Void> removeEntry(
            @PathVariable("entryId") String entryId
    ) {
        libraryService.removeEntry(DEMO_USER_ID, entryId);
        return ResponseEntity.noContent().build();
    }
}