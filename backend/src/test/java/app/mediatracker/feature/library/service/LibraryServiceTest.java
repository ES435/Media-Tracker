package app.mediatracker.feature.library.service;

import app.mediatracker.feature.library.dto.LibraryEntryResponse;
import app.mediatracker.feature.library.model.LibraryEntryStatus;
import app.mediatracker.feature.library.model.UserLibraryEntry;
import app.mediatracker.feature.library.repo.UserLibraryEntryRepository;
import app.mediatracker.feature.library.service.command.ManualEntryCommand;
import app.mediatracker.feature.search.core.dto.SearchResult;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LibraryService.
 * Validates library management logic, including automated entry creation,
 * manual entries, and authorization checks.
 */
class LibraryServiceTest {

    @Mock
    private UserLibraryEntryRepository repository;

    @InjectMocks
    private LibraryService service;

    private ObjectId userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = new ObjectId();
    }

    private UserLibraryEntry sampleEntry(String externalId) {
        return UserLibraryEntry.builder()
                .id("entry-" + externalId)
                .userId(userId)
                // IMPORTANT: lowercase 'book' to match your test data consistency
                .mediaType("book")
                .externalId(externalId)
                .title("Old Title")
                .build();
    }

    private SearchResult sampleSearchResult() {
        return SearchResult.builder()
                .type("book")
                .id("123")
                .title("New Book")
                .imageUrl("http://image.com/book.jpg")
                .sourceUrl("http://source.com/book")
                .meta(Map.of("year", 2023))
                .build();
    }

    @Test
    void getLibraryForUser_returnsMappedResponses() {
        // Arrange
        UserLibraryEntry entry = sampleEntry("123");
        when(repository.findByUserId(userId)).thenReturn(List.of(entry));

        // Act
        List<LibraryEntryResponse> responses = service.getLibraryForUser(userId);

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(entry.getId());
        assertThat(responses.get(0).getMediaItem().getExternalId()).isEqualTo(entry.getExternalId());
    }

    @Test
    void addOrUpdateEntryFromSearchResult_createsNewIfNotExist() {
        // Arrange
        SearchResult sr = sampleSearchResult();

        // Mock: If we search for 'book' and '123' and find nothing -> create new
        when(repository.findByUserIdAndMediaTypeAndExternalId(eq(userId), eq("book"), eq("123")))
                .thenReturn(Optional.empty());

        // Save Mock: returns the object that was passed in with a generated ID
        when(repository.save(any(UserLibraryEntry.class)))
                .thenAnswer(invocation -> {
                    UserLibraryEntry entry = invocation.getArgument(0);
                    entry.setId("generated-id");
                    return entry;
                });

        // Act
        LibraryEntryResponse response = service.addOrUpdateEntryFromSearchResult(
                userId, sr, LibraryEntryStatus.PLANNED, 5, "note");

        // Assert & Verify Capture
        ArgumentCaptor<UserLibraryEntry> captor = ArgumentCaptor.forClass(UserLibraryEntry.class);
        verify(repository).save(captor.capture());

        UserLibraryEntry saved = captor.getValue();
        assertThat(saved.getExternalId()).isEqualTo("123");
        assertThat(saved.getMediaType()).isEqualTo("book");
        assertThat(saved.getStatus()).isEqualTo(LibraryEntryStatus.PLANNED);
        assertThat(saved.getNotes()).isEqualTo("note");
        assertThat(response.getMediaItem().getTitle()).isEqualTo("New Book");
    }

    @Test
    void addManualEntry_createsNewManualEntry() {
        // Arrange
        ManualEntryCommand cmd = ManualEntryCommand.builder()
                .userId(userId)
                .type("movie")
                .title("Manual Movie")
                .status(LibraryEntryStatus.COMPLETED)
                .rating(9)
                .notes("Manual notes")
                .build();

        when(repository.save(any(UserLibraryEntry.class)))
                .thenAnswer(invocation -> {
                    UserLibraryEntry entry = invocation.getArgument(0);
                    entry.setId("manual-id-db");
                    return entry;
                });

        // Act
        LibraryEntryResponse response = service.addManualEntry(cmd);

        // Assert
        assertThat(response.getMediaItem().getTitle()).isEqualTo("Manual Movie");
        assertThat(response.getStatus()).isEqualTo(LibraryEntryStatus.COMPLETED);
        assertThat(response.getMediaItem().getType()).isEqualTo("movie");
        // Manual entries should have a specific prefix in their external ID
        assertThat(response.getMediaItem().getExternalId()).startsWith("manual-");
    }

    @Test
    void updateManualEntry_updatesExistingManualEntry() {
        // Arrange
        UserLibraryEntry existing = sampleEntry("manual-456");
        existing.setExternalId("manual-456");

        when(repository.findById("manual-456")).thenReturn(Optional.of(existing));
        when(repository.save(any(UserLibraryEntry.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ManualEntryCommand cmd = ManualEntryCommand.builder()
                .userId(userId)
                .title("Updated Title")
                .status(LibraryEntryStatus.COMPLETED)
                .build();

        // Act
        LibraryEntryResponse response = service.updateManualEntry("manual-456", cmd);

        // Assert
        assertThat(response.getMediaItem().getTitle()).isEqualTo("Updated Title");
        assertThat(response.getStatus()).isEqualTo(LibraryEntryStatus.COMPLETED);
    }

    @Test
    void updateManualEntry_nonManualEntry_throwsForbidden() {
        // Arrange: ID does not start with manual-
        UserLibraryEntry existing = sampleEntry("123");
        when(repository.findById("123")).thenReturn(Optional.of(existing));

        ManualEntryCommand cmd = ManualEntryCommand.builder()
                .userId(userId)
                .title("Update")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> service.updateManualEntry("123", cmd))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Not a manual entry");
    }

    @Test
    void updateManualEntry_wrongUser_throwsForbidden() {
        // Arrange: Entry belongs to a different user
        UserLibraryEntry existing = sampleEntry("manual-789");
        existing.setUserId(new ObjectId());

        when(repository.findById("manual-789")).thenReturn(Optional.of(existing));

        ManualEntryCommand cmd = ManualEntryCommand.builder()
                .userId(userId) // Attempting update with current user
                .title("Update")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> service.updateManualEntry("manual-789", cmd))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Not your entry");
    }

    @Test
    void removeEntry_deletesOnlyIfUserMatches() {
        // Arrange
        UserLibraryEntry entry = sampleEntry("123");
        when(repository.findById("entry-123")).thenReturn(Optional.of(entry));

        // Act
        service.removeEntry(userId, "entry-123");

        // Assert
        verify(repository).delete(entry);
    }

    @Test
    void removeEntry_doesNothingIfUserMismatch() {
        // Arrange: Entry belongs to someone else
        UserLibraryEntry entry = sampleEntry("123");
        entry.setUserId(new ObjectId());
        when(repository.findById("entry-123")).thenReturn(Optional.of(entry));

        // Act
        service.removeEntry(userId, "entry-123");

        // Assert: Delete should never be called
        verify(repository, never()).delete(entry);
    }
}