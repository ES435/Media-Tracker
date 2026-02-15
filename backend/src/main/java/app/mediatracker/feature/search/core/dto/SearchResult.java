package app.mediatracker.feature.search.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Simple search result DTO.
 * Purpose: Provides a unified, lightweight format for results across media types
 * (e.g., "anime", "movie", "book"). The frontend can render a list without
 * needing to know the source API.
 * Notes:
 * - {@code type} identifies the media type (must match the provider).
 * - {@code meta} carries optional, API-specific extras
 *   (e.g., {"year": 2002, "score": 8.1}).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // do not serialize null fields
public class SearchResult {
    @NotBlank
    private String type;       // e.g., "anime", "movie", "book", "music"
    @NotBlank
    private String id;         // external ID/key from the source
    private String title;      // title/name of the result
    private String imageUrl;   // preview image (optional)
    private String sourceUrl;  // link to the source's detail page (optional)

    // API/type-specific extras (optional), extensible
    private Map<String, Object> meta;
}