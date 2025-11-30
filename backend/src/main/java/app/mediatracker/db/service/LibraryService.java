package app.mediatracker.db.service;

import app.mediatracker.core.dto.SearchResult;
import app.mediatracker.db.api.LibraryEntryResponse;
import app.mediatracker.db.api.MediaItemSummary;
import app.mediatracker.db.domain.LibraryEntryStatus;
import app.mediatracker.db.domain.MediaItem;
import app.mediatracker.db.domain.UserLibraryEntry;
import app.mediatracker.db.repo.MediaItemRepository;
import app.mediatracker.db.repo.UserLibraryEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
                .orElseGet(() -> createMediaItem(searchResult));

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
     * Legt anhand einer Manual Entry Eingabe des Users einen Bibliothekseintrag für einen User an.
     * <p>
     * Dabei wird sichergestellt, dass für die Kombination aus Medientyp und ID genau ein
     * MediaItem existiert (Upsert-Semantik). 
     * Diese ID wird für manual Entry Einträge generiert.
     * Anschließend wird der UserLibraryEntry mit Status,
     * Rating und Notizen gespeichert und als API-DTO zurückgegeben.
     * </p>
     *
     * @param userId        technische User-ID
     * @param type          vom User gewählter Medientyp
     * @param title         vom User gewählter Titel
     * @param author        vom User gewählter Autor
     * @param imageUrl      vom User gewähltes Bild (URL)
     * @param meta          weitere evtl Metadaten
     * @param status        neuer Status des Eintrags (z. B. PLANNED, COMPLETED)
     * @param rating        optionale Bewertung; kann null sein
     * @param notes         optionale Notizen
     * @return angelegter bzw. aktualisierter Eintrag als LibraryEntryResponse
     */
    public LibraryEntryResponse addEntryFromManualEntry(
            String userId, 
            String type, 
            String title,
            String author, 
            String imageUrl, 
            Map<String,Object> meta, 
            LibraryEntryStatus status, 
            Integer rating,
            String notes
    ) {
        String manualId = "manual-" + UUID.randomUUID();
        MediaItem mediaItem = mediaItemRepository
                .findByTypeAndExternalId(type, manualId)
                .orElseGet(() -> createMediaItem(type, manualId, title, author, imageUrl, meta));

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

    private MediaItem createMediaItem(SearchResult searchResult) {
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

    //Overload createMediaItem() um auch mit Manual Entry Daten ein MediaItem erstellen zu können
    private MediaItem createMediaItem(String type, String manualId, String title, String author, String imageUrl, Map<String, Object> meta) {
        // Auch hier: nur Getter
        MediaItem mediaItem = MediaItem.builder()
                .type(type)
                .externalId(manualId)
                .title(title)
                .imageUrl(imageUrl)
                .sourceUrl(null)
                .meta(meta)
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