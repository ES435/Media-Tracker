package app.mediatracker.db.controller;

import app.mediatracker.db.api.AddLibraryEntryRequest;
import app.mediatracker.db.api.AddManualEntryRequest;
import app.mediatracker.db.api.LibraryEntryResponse;
import app.mediatracker.db.domain.LibraryEntryStatus;
import app.mediatracker.db.service.LibraryService;
import app.mediatracker.search.core.dto.SearchResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-Controller für die persönliche Medienbibliothek.
 * <p>
 * Stellt Endpunkte zum Lesen, Anlegen/Aktualisieren und Löschen von Bibliothekseinträgen bereit.
 * Das Frontend greift über diese Endpunkte zu. In dieser Demo wird ein statischer Demo-User verwendet
 * (siehe DEMO_USER_ID); in einer echten Anwendung würde die User-ID aus dem Security-Kontext stammen.
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
     * Legt einen neuen Bibliothekseintrag an oder aktualisiert einen bestehenden für den Demo-User.
     * <p>
     * Medien-Basisdaten (Typ, externe ID, Titel, Bild, Quelle) werden als Snapshot direkt im Eintrag gespeichert,
     * es gibt keine separate MediaItem-Collection mehr.
     * </p>
     *
     * @param request Payload mit Status/Rating/Notizen sowie dem ausgewählten SearchResult
     * @return 200 OK mit dem gespeicherten/aktualisierten Eintrag
     */
    @PostMapping
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
     * Legt basierend auf Manual Entry Daten einen neuen Bibliothekseintrag (MediaItem) für den Demo-User an.
     * <p>
     * Zusätzlich werden (Status, Rating, Notizen) gespeichert.
     * </p>
     *
     * @param request Payload mit Status/Rating/Notizen sowie dem ausgewählten SearchResult
     * @return 200 OK mit dem gespeicherten/aktualisierten Eintrag
     */
    @PostMapping("/addManualEntry")
    public ResponseEntity<LibraryEntryResponse> addManualEntry(
            @RequestBody AddManualEntryRequest request
        ) {
            LibraryEntryResponse response = libraryService.addEntryFromManualEntry(
                DEMO_USER_ID,
                request.getType(),
                request.getTitle(),
                request.getAuthor(),
                // request.getGenre(),
                request.getImageUrl(),
                request.getMeta(),
                request.getStatus(),
                request.getRating(),
                request.getNotes()
            );
        
            return ResponseEntity.ok(response);
    }
    

    /**
     * Entfernt einen Bibliothekseintrag des Demo-Users, sofern er dem User gehört.
     *
     * @param entryId technische ID des Eintrags (MongoDB-ID)
     * @return 204 No Content, unabhängig davon, ob der Eintrag existierte oder nicht
     */
    @DeleteMapping("/{entryId}")
    public ResponseEntity<Void> removeEntry(
            @PathVariable("entryId") String entryId
    ) {
        libraryService.removeEntry(DEMO_USER_ID, entryId);
        return ResponseEntity.noContent().build();
    }
}