package app.mediatracker.feature.library.service.command;

import app.mediatracker.feature.library.model.LibraryEntryStatus;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ManualEntryCommand {
    private String userId;
    private String type;
    private String title;
    private String author;
    private String imageUrl;
    private Map<String, Object> meta;
    private LibraryEntryStatus status;
    private Integer rating;
    private String notes;
}