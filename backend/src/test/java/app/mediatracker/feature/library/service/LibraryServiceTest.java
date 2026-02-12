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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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
                .mediaType("BOOK")
                .externalId(externalId)
                .title("Old Title")
                .build();
    }

    private SearchResult sampleSearchResult() {
        return SearchResult.builder()
                .type("BOOK")
                .id("123")
                .title("New Book")
                .imageUrl("http://image.com/book.jpg")
                .sourceUrl("http://source.com/book")
                .meta(Map.of("year", 2023))
                .build();
    }

    @Test
    void getLibraryForUser_returnsMappedResponses() {
        UserLibraryEntry entry = sampleEntry("123");
        when(repository.findByUserId(userId)).thenReturn(List.of(entry));

        List<LibraryEntryResponse> responses = service.getLibraryForUser(userId);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(entry.getId());
        assertThat(responses.get(0).getMediaItem().getExternalId()).isEqualTo(entry.getExternalId());
    }


    @Test
    void addOrUpdateEntryFromSearchResult_createsNewIfNotExist() {
        SearchResult sr = sampleSearchResult();
        when(repository.findByUserIdAndMediaTypeAndExternalId(userId, sr.getType(), sr.getId()))
                .thenReturn(Optional.empty());

        // save korrekt mocken
        when(repository.save(any(UserLibraryEntry.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LibraryEntryResponse response = service.addOrUpdateEntryFromSearchResult(
                userId, sr, LibraryEntryStatus.PLANNED, 5, "note");

        ArgumentCaptor<UserLibraryEntry> captor = ArgumentCaptor.forClass(UserLibraryEntry.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue().getExternalId()).isEqualTo("123");
        assertThat(captor.getValue().getStatus()).isEqualTo(LibraryEntryStatus.PLANNED);
        assertThat(captor.getValue().getNotes()).isEqualTo("note");

        assertThat(response.getMediaItem().getTitle()).isEqualTo("New Book");
    }


    @Test
    void addManualEntry_createsNewManualEntry() {
        ManualEntryCommand cmd = ManualEntryCommand.builder()
                .userId(userId)
                .type("MOVIE")
                .title("Manual Movie")
                .status(LibraryEntryStatus.COMPLETED)
                .rating(9)
                .notes("Manual notes")
                .build();

        when(repository.findByUserIdAndMediaTypeAndExternalId(any(), any(), any()))
                .thenReturn(Optional.empty());

        // save korrekt mocken
        when(repository.save(any(UserLibraryEntry.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LibraryEntryResponse response = service.addManualEntry(cmd);

        assertThat(response.getMediaItem().getTitle()).isEqualTo("Manual Movie");
        assertThat(response.getStatus()).isEqualTo(LibraryEntryStatus.COMPLETED);
        assertThat(response.getMediaItem().getType()).isEqualTo("MOVIE");
        assertThat(response.getMediaItem().getExternalId()).startsWith("manual-");
    }


    @Test
    void updateManualEntry_updatesExistingManualEntry() {
        UserLibraryEntry existing = sampleEntry("manual-456");
        existing.setExternalId("manual-456");
        when(repository.findById("manual-456")).thenReturn(Optional.of(existing));

        // save korrekt mocken
        when(repository.save(any(UserLibraryEntry.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ManualEntryCommand cmd = ManualEntryCommand.builder()
                .userId(userId)
                .title("Updated Title")
                .status(LibraryEntryStatus.COMPLETED)
                .build();

        LibraryEntryResponse response = service.updateManualEntry("manual-456", cmd);

        assertThat(response.getMediaItem().getTitle()).isEqualTo("Updated Title");
        assertThat(response.getStatus()).isEqualTo(LibraryEntryStatus.COMPLETED);
    }

    @Test
    void updateManualEntry_nonManualEntry_throwsForbidden() {
        UserLibraryEntry existing = sampleEntry("123");
        when(repository.findById("123")).thenReturn(Optional.of(existing));

        ManualEntryCommand cmd = ManualEntryCommand.builder()
                .userId(userId)
                .title("Update")
                .build();

        assertThatThrownBy(() -> service.updateManualEntry("123", cmd))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Not a manual entry");
    }

    @Test
    void updateManualEntry_wrongUser_throwsForbidden() {
        UserLibraryEntry existing = sampleEntry("manual-789");
        existing.setUserId(new ObjectId()); // anderer User
        when(repository.findById("manual-789")).thenReturn(Optional.of(existing));

        ManualEntryCommand cmd = ManualEntryCommand.builder()
                .userId(userId)
                .title("Update")
                .build();

        assertThatThrownBy(() -> service.updateManualEntry("manual-789", cmd))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Not your entry");
    }


    @Test
    void removeEntry_deletesOnlyIfUserMatches() {
        UserLibraryEntry entry = sampleEntry("123");
        when(repository.findById("entry-123")).thenReturn(Optional.of(entry));

        service.removeEntry(userId, "entry-123");

        verify(repository).delete(entry);
    }

    @Test
    void removeEntry_doesNothingIfUserMismatch() {
        UserLibraryEntry entry = sampleEntry("123");
        entry.setUserId(new ObjectId()); // anderer User
        when(repository.findById("entry-123")).thenReturn(Optional.of(entry));

        service.removeEntry(userId, "entry-123");

        verify(repository, never()).delete(entry);
    }
}
