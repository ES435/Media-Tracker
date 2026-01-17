package app.mediatracker.feature.library.service;

import app.mediatracker.search.core.dto.SearchResult;
import app.mediatracker.feature.library.dto.LibraryEntryResponse;
import app.mediatracker.feature.library.dto.MediaItemSummary;
import app.mediatracker.feature.library.model.LibraryEntryStatus;
import app.mediatracker.feature.library.model.UserLibraryEntry;
import app.mediatracker.feature.library.repo.UserLibraryEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
    public LibraryEntryResponse addManualEntry(
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
        UserLibraryEntry entry = userLibraryEntryRepository
                .findByUserIdAndMediaTypeAndExternalId(userId, type, manualId)
                .orElseGet(() -> newUserLibraryEntry(userId, type, manualId, title, author, imageUrl, meta));

        // Aktualisiere nutzerspezifische Felder
        entry.setStatus(status);
        entry.setRating(rating);
        entry.setNotes(notes);

        UserLibraryEntry saved = userLibraryEntryRepository.save(entry);
        return toResponse(saved);
    }

    /**
     * Legt anhand eines Suchergebnisses (SearchResult) einen Bibliothekseintrag für einen User an
     * oder aktualisiert einen vorhandenen Eintrag.
     */
    public LibraryEntryResponse updateManualEntry(
            String userId, 
            String entryId,
            String type, 
            String title,
            String author, 
            String imageUrl, 
            Map<String,Object> meta, 
            LibraryEntryStatus status, 
            Integer rating,
            String notes
    ) {
        // Check ob manual entry existiert (und ein manual entry ist)
        UserLibraryEntry entry = userLibraryEntryRepository.findById(entryId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found"));

        if (!entry.getExternalId().startsWith("manual-")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a manual entry");
        }
        //Update
        if (type != null) entry.setMediaType(type);
        if (title != null) entry.setTitle(title);
        if (author != null) entry.setAuthor(author);
        if (imageUrl != null) entry.setImageUrl(imageUrl);
        if (meta != null) entry.setMeta(meta);
        if (status != null) entry.setStatus(status);
        if (rating != null) entry.setRating(rating);
        if (notes != null) entry.setNotes(notes);

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
                .meta(searchResult.getMeta())
                .build();
    }


    //Overload newUserLibraryEntry() um auch mit Manual Entry Daten ein LibraryEntry eines MediaItems erstellen zu können
    private UserLibraryEntry newUserLibraryEntry(String userId, String type, String manualId, String title, String author, String imageUrl, Map<String, Object> meta) {
        return UserLibraryEntry.builder()
                .userId(userId)
                .mediaType(type)
                .externalId(manualId)
                .title(title)
                .imageUrl(imageUrl)
                .meta(meta)
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