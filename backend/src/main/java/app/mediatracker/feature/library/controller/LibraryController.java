package app.mediatracker.feature.library.controller;

import app.mediatracker.feature.library.dto.AddLibraryEntryRequest;
import app.mediatracker.feature.library.dto.LibraryEntryResponse;
import app.mediatracker.feature.library.dto.ManualEntryRequest;
import app.mediatracker.feature.library.model.LibraryEntryStatus;
import app.mediatracker.feature.library.service.LibraryService;
import app.mediatracker.feature.library.service.command.ManualEntryCommand;
import app.mediatracker.feature.search.core.dto.SearchResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-Controller für die persönliche Medienbibliothek.
 * <p>
 * Stellt Endpunkte zum Lesen, Anlegen/Aktualisieren und Löschen von Bibliothekseinträgen bereit.
 * </p>
 */
@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {

    private static final String DEMO_USER_ID = "user_x";

    private final LibraryService libraryService;

    /**
     * Liefert die komplette Bibliothek des Demo-Users als Liste von LibraryEntryResponse.
     *
     * @return 200 OK mit allen Einträgen des Users in Anzeigeform
     */
    @GetMapping
    public ResponseEntity<List<LibraryEntryResponse>> getLibrary() {
        List<LibraryEntryResponse> entries = libraryService.getLibraryForUser(DEMO_USER_ID);
        return ResponseEntity.ok(entries);
    }

    /**
     * Paginierte Ansicht der Bibliothek, standardmäßig nach updatedAt DESC sortiert.
     */
    @GetMapping("/page")
    public ResponseEntity<Page<LibraryEntryResponse>> getLibraryPage(
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<LibraryEntryResponse> page = libraryService.getLibraryForUser(DEMO_USER_ID, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Legt einen neuen Bibliothekseintrag an oder aktualisiert einen bestehenden (basierend auf SearchResult).
     *
     * @param request Payload mit Status/Rating/Notizen sowie dem ausgewählten SearchResult
     * @return 200 OK mit dem gespeicherten/aktualisierten Eintrag
     */
    @PostMapping()
    public ResponseEntity<LibraryEntryResponse> addOrUpdateEntry(
            @Valid @RequestBody AddLibraryEntryRequest request
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

    /**
     * Legt basierend auf Manual Entry Daten einen neuen Bibliothekseintrag an.
     * Nutzt das Clean Code Command Pattern.
     *
     * @param request Payload mit den manuellen Daten
     * @return 200 OK mit dem gespeicherten Eintrag
     */
    @PostMapping("/manualEntry")
    public ResponseEntity<LibraryEntryResponse> addManualEntry(
            @Valid @RequestBody ManualEntryRequest request
    ) {

        ManualEntryCommand command = ManualEntryCommand.builder()
                .userId(DEMO_USER_ID)
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
     * Lässt den User einen bestehenden Manual Entry bearbeiten.
     * Nutzt das Clean Code Command Pattern.
     *
     * @param request Payload mit den aktualisierten Daten
     * @param entryId ID des Eintrags
     * @return 200 OK mit dem aktualisierten Eintrag
     */
    @PatchMapping("/manualEntry/{entryId}")
    public ResponseEntity<LibraryEntryResponse> updateManualEntry(
            @RequestBody ManualEntryRequest request,
            @PathVariable("entryId") String entryId
    ) {

        ManualEntryCommand command = ManualEntryCommand.builder()
                .userId(DEMO_USER_ID)
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
     * Entfernt einen Bibliothekseintrag des Demo-Users.
     *
     * @param entryId technische ID des Eintrags (MongoDB-ID)
     * @return 204 No Content
     */
    @DeleteMapping("/{entryId}")
    public ResponseEntity<Void> removeEntry(
            @PathVariable("entryId") String entryId
    ) {
        libraryService.removeEntry(DEMO_USER_ID, entryId);
        return ResponseEntity.noContent().build();
    }
}