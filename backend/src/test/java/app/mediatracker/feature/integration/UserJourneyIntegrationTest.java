package app.mediatracker.feature.integration;

import app.mediatracker.feature.library.model.LibraryEntryStatus;
import app.mediatracker.feature.library.model.UserLibraryEntry;
import app.mediatracker.feature.library.repo.UserLibraryEntryRepository;
import app.mediatracker.feature.library.service.LibraryService;
import app.mediatracker.feature.library.service.command.ManualEntryCommand;
import app.mediatracker.feature.user.repo.UserRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration test for the end-to-end user journey.
 * Verifies that a user can successfully create and persist a manual library entry.
 */
@SpringBootTest
class UserJourneyIntegrationTest {

    @Autowired
    private LibraryService libraryService;

    @Autowired
    private UserLibraryEntryRepository libraryRepository;

    @Autowired
    private UserRepository userRepository;

    private ObjectId testUserId;

    /**
     * Resets the database state before each test to ensure isolation.
     */
    @BeforeEach
    void setUp() {
        // Clear database collections
        libraryRepository.deleteAll();
        userRepository.deleteAll();

        testUserId = new ObjectId();
    }

    @Test
    void userCanAddMovieToLibrary() {
        // Step 1: Create a manual entry command
        ManualEntryCommand command = ManualEntryCommand.builder()
                .userId(testUserId)
                .type("movie")
                .title("Naruto")
                .author("Studio Pierrot")
                .status(LibraryEntryStatus.PLANNED)
                .rating(8)
                .notes("My favorite series")
                .meta(Collections.emptyMap())
                .imageUrl("http://image.url/naruto.jpg")
                .build();

        // Step 2: Execute the service call to add the entry
        libraryService.addManualEntry(command);

        // Step 3: Verify that the movie exists in the embedded database
        List<UserLibraryEntry> entries = libraryRepository.findByUserId(testUserId);
        assertEquals(1, entries.size(), "There should be exactly one entry in the library");

        UserLibraryEntry entry = entries.get(0);
        assertEquals("Naruto", entry.getTitle());
        assertEquals("movie", entry.getMediaType());
        assertEquals(LibraryEntryStatus.PLANNED, entry.getStatus());
        assertEquals(8, entry.getRating());
        assertEquals("My favorite series", entry.getNotes());
    }
}