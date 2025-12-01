package app.mediatracker.db.service;

import app.mediatracker.search.core.dto.SearchResult;
import app.mediatracker.db.api.LibraryEntryResponse;
import app.mediatracker.db.api.MediaItemSummary;
import app.mediatracker.db.domain.LibraryEntryStatus;
import app.mediatracker.db.domain.UserLibraryEntry;
import app.mediatracker.db.repo.UserLibraryEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Anwendungslogik für die Medienbibliothek.
 * <p>
 * Diese Service-Klasse koordiniert die Speicherung und Abfrage von Bibliothekseinträgen eines Users.
 * In dieser Variante werden Medien-Basisdaten direkt im Eintrag als Snapshot gespeichert (kein separates MediaItem).
 * </p>
 * <p>
 * Persistenz: MongoDB über Spring Data Repositories. Zeitstempel werden durch Mongo Auditing gesetzt.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class LibraryService {

    private final UserLibraryEntryRepository userLibraryEntryRepository;

    /**
     * Liefert alle Bibliothekseinträge eines Users inkl. Medien-Snapshot.
     */
    public List<LibraryEntryResponse> getLibraryForUser(String userId) {
        List<UserLibraryEntry> entries = userLibraryEntryRepository.findByUserId(userId);
        return entries.stream().map(this::toResponse).toList();
    }

    /**
     * Paginierte Bibliothek eines Users, standardmäßig nach updatedAt DESC sortiert.
     */
    public Page<LibraryEntryResponse> getLibraryForUser(String userId, Pageable pageable) {
        Page<UserLibraryEntry> page = userLibraryEntryRepository.findByUserId(userId, pageable);
        List<LibraryEntryResponse> content = page.getContent().stream().map(this::toResponse).toList();
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    /**
     * Legt anhand eines Suchergebnisses (SearchResult) einen Bibliothekseintrag für einen User an
     * oder aktualisiert einen vorhandenen Eintrag.
     */
    public LibraryEntryResponse addOrUpdateEntryFromSearchResult(
            String userId,
            SearchResult searchResult,
            LibraryEntryStatus status,
            Integer rating,
            String notes
    ) {
        UserLibraryEntry entry = userLibraryEntryRepository
                .findByUserIdAndMediaTypeAndExternalId(userId, searchResult.getType(), searchResult.getId())
                .orElseGet(() -> newUserLibraryEntry(userId, searchResult));

        // Aktualisiere nutzerspezifische Felder
        entry.setStatus(status);
        entry.setRating(rating);
        entry.setNotes(notes);

        // Optional: Medien-Snapshot aktualisieren (z. B. Titeländerung)
        entry.setTitle(searchResult.getTitle());
        entry.setImageUrl(searchResult.getImageUrl());
        entry.setSourceUrl(searchResult.getSourceUrl());

        UserLibraryEntry saved = userLibraryEntryRepository.save(entry);
        return toResponse(saved);
    }

    /**
     * Entfernt einen Bibliothekseintrag eines Users, falls der Eintrag diesem User gehört.
     */
    public void removeEntry(String userId, String entryId) {
        Optional<UserLibraryEntry> maybeEntry = userLibraryEntryRepository.findById(entryId);
        maybeEntry.ifPresent(entry -> {
            if (userId.equals(entry.getUserId())) {
                userLibraryEntryRepository.delete(entry);
            }
        });
    }

    private UserLibraryEntry newUserLibraryEntry(String userId, SearchResult searchResult) {
        return UserLibraryEntry.builder()
                .userId(userId)
                .mediaType(searchResult.getType())
                .externalId(searchResult.getId())
                .title(searchResult.getTitle())
                .imageUrl(searchResult.getImageUrl())
                .sourceUrl(searchResult.getSourceUrl())
                .build();
    }

    private LibraryEntryResponse toResponse(UserLibraryEntry entry) {
        MediaItemSummary mediaSummary = MediaItemSummary.builder()
                // internes MediaItem wird nicht separat persistiert; Snapshot-Felder stammen aus dem Eintrag
                .type(entry.getMediaType())
                .externalId(entry.getExternalId())
                .title(entry.getTitle())
                .imageUrl(entry.getImageUrl())
                .sourceUrl(entry.getSourceUrl())
                .build();

        return LibraryEntryResponse.builder()
                .id(entry.getId())
                .userId(entry.getUserId())
                .status(entry.getStatus())
                .rating(entry.getRating())
                .notes(entry.getNotes())
                .createdAt(entry.getCreatedAt())
                .updatedAt(entry.getUpdatedAt())
                .mediaItem(mediaSummary)
                .build();
    }
}