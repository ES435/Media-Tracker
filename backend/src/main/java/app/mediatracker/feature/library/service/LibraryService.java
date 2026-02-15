package app.mediatracker.feature.library.service;

import app.mediatracker.feature.library.dto.LibraryEntryResponse;
import app.mediatracker.feature.library.dto.MediaItemSummary;
import app.mediatracker.feature.library.model.LibraryEntryStatus;
import app.mediatracker.feature.library.model.UserLibraryEntry;
import app.mediatracker.feature.library.repo.UserLibraryEntryRepository;
import app.mediatracker.feature.library.service.command.ManualEntryCommand;
import app.mediatracker.feature.search.core.dto.SearchResult;
import lombok.RequiredArgsConstructor;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing the user's personal media library.
 * <p>
 * Handles business logic for adding, updating, retrieving, and removing
 * library entries (both from search results and manual entries).
 * </p>
 */
@Service
@RequiredArgsConstructor
public class LibraryService {

    private final UserLibraryEntryRepository userLibraryEntryRepository;

    /**
     * Retrieves the complete library for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of library entry responses
     */
    public List<LibraryEntryResponse> getLibraryForUser(ObjectId userId) {
        List<UserLibraryEntry> entries = userLibraryEntryRepository.findByUserId(userId);
        return entries.stream().map(this::toResponse).toList();
    }

    /**
     * Retrieves a paginated view of the user's library.
     *
     * @param userId   the ID of the user
     * @param pageable pagination information
     * @return a page of library entry responses
     */
    public Page<LibraryEntryResponse> getLibraryForUser(ObjectId userId, Pageable pageable) {
        Page<UserLibraryEntry> page = userLibraryEntryRepository.findByUserId(userId, pageable);
        List<LibraryEntryResponse> content = page.getContent().stream().map(this::toResponse).toList();
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    /**
     * Adds a new entry based on a search result or updates an existing one if it already exists.
     *
     * @param userId       the ID of the user
     * @param searchResult the selected search result
     * @param status       the status (e.g., PLANNED, COMPLETED)
     * @param rating       optional rating
     * @param notes        optional notes
     * @return the saved library entry response
     */
    public LibraryEntryResponse addOrUpdateEntryFromSearchResult(
            ObjectId userId,
            SearchResult searchResult,
            LibraryEntryStatus status,
            Integer rating,
            String notes
    ) {
        UserLibraryEntry entry = userLibraryEntryRepository
                .findByUserIdAndMediaTypeAndExternalId(userId, searchResult.getType(), searchResult.getId())
                .orElseGet(() -> newUserLibraryEntry(userId, searchResult));

        updateEntryUserFields(entry, status, rating, notes);

        // Always update media details in case they changed in the source
        entry.setTitle(searchResult.getTitle());
        entry.setImageUrl(searchResult.getImageUrl());
        entry.setSourceUrl(searchResult.getSourceUrl());

        return toResponse(userLibraryEntryRepository.save(entry));
    }

    /**
     * Adds a manually created entry to the library.
     *
     * @param command the command object containing manual entry details
     * @return the saved library entry response
     */
    public LibraryEntryResponse addManualEntry(ManualEntryCommand command) {
        // ID generation for manual entries
        String manualId = "manual-" + UUID.randomUUID();

        // Check if entry already exists (unlikely with random UUID, but safety first)
        UserLibraryEntry entry = userLibraryEntryRepository
                .findByUserIdAndMediaTypeAndExternalId(command.getUserId(), command.getType(), manualId)
                .orElseGet(() -> newUserLibraryEntryFromCommand(command, manualId));

        updateEntryUserFields(entry, command.getStatus(), command.getRating(), command.getNotes());

        return toResponse(userLibraryEntryRepository.save(entry));
    }

    /**
     * Updates an existing manual entry.
     *
     * @param entryId the ID of the entry to update
     * @param command the command object containing updated details
     * @return the updated library entry response
     * @throws ResponseStatusException if the entry is not found, not manual, or belongs to another user
     */
    public LibraryEntryResponse updateManualEntry(String entryId, ManualEntryCommand command) {
        UserLibraryEntry entry = userLibraryEntryRepository.findById(entryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found"));

        if (!entry.getExternalId().startsWith("manual-")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a manual entry");
        }

        // Ownership Check
        if (!entry.getUserId().equals(command.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your entry");
        }

        // Update Media Fields
        if (command.getType() != null) entry.setMediaType(command.getType());
        if (command.getTitle() != null) entry.setTitle(command.getTitle());
        if (command.getAuthor() != null) entry.setAuthor(command.getAuthor());
        if (command.getImageUrl() != null) entry.setImageUrl(command.getImageUrl());
        if (command.getMeta() != null) entry.setMeta(command.getMeta());

        // Update User Fields
        updateEntryUserFields(entry, command.getStatus(), command.getRating(), command.getNotes());

        return toResponse(userLibraryEntryRepository.save(entry));
    }

    /**
     * Removes an entry from the library, ensuring ownership.
     *
     * @param userId  the ID of the requesting user
     * @param entryId the ID of the entry to remove
     */
    public void removeEntry(ObjectId userId, String entryId) {
        userLibraryEntryRepository.findById(entryId).ifPresent(entry -> {
            if (userId.equals(entry.getUserId())) {
                userLibraryEntryRepository.delete(entry);
            }
        });
    }


    // --- Helper Methods ---

    private void updateEntryUserFields(UserLibraryEntry entry, LibraryEntryStatus status, Integer rating, String notes) {
        if (status != null) entry.setStatus(status);
        if (rating != null) entry.setRating(rating);
        if (notes != null) entry.setNotes(notes);
    }

    private UserLibraryEntry newUserLibraryEntry(ObjectId userId, SearchResult searchResult) {
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

    private UserLibraryEntry newUserLibraryEntryFromCommand(ManualEntryCommand command, String manualId) {
        return UserLibraryEntry.builder()
                .userId(command.getUserId())
                .mediaType(command.getType())
                .externalId(manualId)
                .title(command.getTitle())
                .imageUrl(command.getImageUrl())
                .meta(command.getMeta())
                .author(command.getAuthor())
                .build();
    }

    private LibraryEntryResponse toResponse(UserLibraryEntry entry) {
        MediaItemSummary mediaSummary = MediaItemSummary.builder()
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