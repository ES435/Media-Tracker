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

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final MediaItemRepository mediaItemRepository;
    private final UserLibraryEntryRepository userLibraryEntryRepository;

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