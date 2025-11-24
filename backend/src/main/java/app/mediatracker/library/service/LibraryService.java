package app.mediatracker.library.service;

import app.mediatracker.core.dto.SearchResult;
import app.mediatracker.library.api.LibraryEntryResponse;
import app.mediatracker.library.api.MediaItemSummary;
import app.mediatracker.library.domain.LibraryEntryStatus;
import app.mediatracker.library.domain.MediaItem;
import app.mediatracker.library.domain.UserLibraryEntry;
import app.mediatracker.library.repo.MediaItemRepository;
import app.mediatracker.library.repo.UserLibraryEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Anwendungslogik für die persönliche Medienbibliothek.
 * <p>
 * Diese Service-Klasse koordiniert die Speicherung und Abfrage von Bibliothekseinträgen eines Users.
 * Sie sorgt dafür, dass zu einem gewählten Suchergebnis (SearchResult) genau ein MediaItem in der Datenbank
 * existiert (identifiziert durch Kombination aus Typ und externer ID) und legt bzw. aktualisiert den
 * dazugehörigen UserLibraryEntry. Außerdem werden die Daten für API-Antworten in ein kompaktes DTO
 * (LibraryEntryResponse) transformiert.
 * </p>
 * <p>
 * Persistenz: MongoDB über Spring Data Repositories. Zeitstempel werden serverseitig mit Instant gesetzt.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class LibraryService {

    private final MediaItemRepository mediaItemRepository;
    private final UserLibraryEntryRepository userLibraryEntryRepository;

    /**
     * Liefert alle Bibliothekseinträge eines Users inkl. zugehöriger MediaItem-Daten.
     *
     * Die Methode lädt zunächst die UserLibraryEntries, ermittelt daraus die referenzierten MediaItem-IDs
     * und liest diese in einem Schwung aus, um N+1-Zugriffe zu vermeiden. Anschließend werden die Daten in
     * LibraryEntryResponse-DTOs transformiert.
     *
     * @param userId technische User-ID (z. B. aus dem Security-Kontext)
     * @return Liste mit allen Einträgen des Users in Anzeigeform
     */
    public List<LibraryEntryResponse> getLibraryForUser(String userId) {
        List<UserLibraryEntry> entries = userLibraryEntryRepository.findByUserId(userId);
        List<String> mediaItemIds = entries.stream()
                .map(UserLibraryEntry::getMediaItemId)
                .distinct()
                .toList();

        List<MediaItem> mediaItems = mediaItemRepository.findAllById(mediaItemIds);

        return entries.stream()
                .map(entry -> toResponse(entry, findMediaItem(mediaItems, entry.getMediaItemId())))
                .toList();
    }

    /**
     * Legt anhand eines Suchergebnisses (SearchResult) einen Bibliothekseintrag für einen User an
     * oder aktualisiert einen vorhandenen Eintrag.
     * <p>
     * Dabei wird sichergestellt, dass für die Kombination aus Medientyp und externer ID genau ein
     * MediaItem existiert (Upsert-Semantik). Anschließend wird der UserLibraryEntry mit Status,
     * Rating und Notizen gespeichert und als API-DTO zurückgegeben.
     * </p>
     *
     * @param userId        technische User-ID
     * @param searchResult  das gewählte Suchergebnis (Quelle: externe Provider)
     * @param status        neuer Status des Eintrags (z. B. PLANNED, COMPLETED)
     * @param rating        optionale Bewertung; kann null sein
     * @param notes         optionale Notizen
     * @return angelegter bzw. aktualisierter Eintrag als LibraryEntryResponse
     */
    public LibraryEntryResponse addOrUpdateEntryFromSearchResult(
            String userId,
            SearchResult searchResult,
            LibraryEntryStatus status,
            Integer rating,
            String notes
    ) {
        // WICHTIG: Getter benutzen, nicht direkt auf Felder zugreifen
        MediaItem mediaItem = mediaItemRepository
                .findByTypeAndExternalId(searchResult.getType(), searchResult.getId())
                .orElseGet(() -> createMediaItemFromSearchResult(searchResult));

        UserLibraryEntry entry = userLibraryEntryRepository
                .findByUserIdAndMediaItemId(userId, mediaItem.getId())
                .orElseGet(() -> newUserLibraryEntry(userId, mediaItem.getId()));

        entry.setStatus(status);
        entry.setRating(rating);
        entry.setNotes(notes);
        entry.setUpdatedAt(Instant.now());

        UserLibraryEntry saved = userLibraryEntryRepository.save(entry);

        return toResponse(saved, mediaItem);
    }

    /**
     * Entfernt einen Bibliothekseintrag eines Users, falls der Eintrag diesem User gehört.
     *
     * @param userId  technische User-ID
     * @param entryId ID des zu löschenden Eintrags
     */
    public void removeEntry(String userId, String entryId) {
        Optional<UserLibraryEntry> maybeEntry = userLibraryEntryRepository.findById(entryId);

        maybeEntry.ifPresent(entry -> {
            if (userId.equals(entry.getUserId())) {
                userLibraryEntryRepository.delete(entry);
            }
        });
    }

    private MediaItem createMediaItemFromSearchResult(SearchResult searchResult) {
        // Auch hier: nur Getter
        MediaItem mediaItem = MediaItem.builder()
                .type(searchResult.getType())
                .externalId(searchResult.getId())
                .title(searchResult.getTitle())
                .imageUrl(searchResult.getImageUrl())
                .sourceUrl(searchResult.getSourceUrl())
                .meta(searchResult.getMeta())
                .build();

        return mediaItemRepository.save(mediaItem);
    }

    private UserLibraryEntry newUserLibraryEntry(String userId, String mediaItemId) {
        Instant now = Instant.now();

        return UserLibraryEntry.builder()
                .userId(userId)
                .mediaItemId(mediaItemId)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private LibraryEntryResponse toResponse(UserLibraryEntry entry, MediaItem mediaItem) {
        MediaItemSummary mediaSummary = null;

        if (mediaItem != null) {
            mediaSummary = MediaItemSummary.builder()
                    .id(mediaItem.getId())
                    .type(mediaItem.getType())
                    .externalId(mediaItem.getExternalId())
                    .title(mediaItem.getTitle())
                    .imageUrl(mediaItem.getImageUrl())
                    .sourceUrl(mediaItem.getSourceUrl())
                    .build();
        }

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

    private MediaItem findMediaItem(List<MediaItem> items, String id) {
        if (id == null) {
            return null;
        }
        return items.stream()
                .filter(item -> id.equals(item.getId()))
                .findFirst()
                .orElse(null);
    }
}