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
     * Legt einen neuen Bibliothekseintrag an oder aktualisiert einen bestehenden für den Demo-User.
     * <p>
     * Falls das zugehörige MediaItem (definiert durch Typ und externe ID aus dem SearchResult) noch nicht existiert,
     * wird es angelegt. Anschließend wird der Eintrag des Users (Status, Rating, Notizen) gespeichert.
     * </p>
     *
     * @param request Payload mit Status/Rating/Notizen sowie dem ausgewählten SearchResult
     * @return 200 OK mit dem gespeicherten/aktualisierten Eintrag
     */
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