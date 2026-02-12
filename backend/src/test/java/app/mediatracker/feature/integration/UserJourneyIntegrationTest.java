package app.mediatracker.feature.integration;

import app.mediatracker.feature.library.model.LibraryEntryStatus;
import app.mediatracker.feature.library.model.UserLibraryEntry;
import app.mediatracker.feature.library.repo.UserLibraryEntryRepository;
import app.mediatracker.feature.library.service.LibraryService;
import app.mediatracker.feature.library.service.command.ManualEntryCommand;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserJourneyIntegrationTest {

    @Autowired
    private LibraryService libraryService;

    @Autowired
    private UserLibraryEntryRepository repository;

    private ObjectId testUserId;

    @BeforeEach
    void setUp() {
        // Leere In-Memory DB vor jedem Test
        repository.deleteAll();
        testUserId = new ObjectId(); // Dummy User
    }

    @Test
    void userCanAddMovieToLibrary() {
        // --- Schritt 1: User "registriert" sich ---
        // Wir simulieren einfach einen User mit testUserId

        // --- Schritt 2: User speichert einen Film ---
        ManualEntryCommand command = ManualEntryCommand.builder()
                .userId(testUserId)
                .type("movie")
                .title("Naruto")
                .author("Studio Pierrot")
                .status(LibraryEntryStatus.PLANNED)
                .rating(8)
                .notes("Meine Lieblingsserie")
                .meta(Collections.emptyMap())
                .imageUrl("http://image.url/naruto.jpg")
                .build();

        libraryService.addManualEntry(command);

        // --- Schritt 3: Prüfen, dass der Film in der DB ist ---
        List<UserLibraryEntry> entries = repository.findByUserId(testUserId);
        assertEquals(1, entries.size(), "Es sollte genau ein Eintrag existieren");

        UserLibraryEntry entry = entries.get(0);
        assertEquals("Naruto", entry.getTitle());
        assertEquals("movie", entry.getMediaType());
        assertEquals(LibraryEntryStatus.PLANNED, entry.getStatus());
        assertEquals(8, entry.getRating());
        assertEquals("Meine Lieblingsserie", entry.getNotes());
    }
}
