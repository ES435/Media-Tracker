package app.mediatracker.feature.library.model;

/**
 * Status of a library entry from the user's perspective.
 * <ul>
 * <li>PLANNED – planned to be consumed (read, watched, played).</li>
 * <li>IN_PROGRESS – currently in progress.</li>
 * <li>COMPLETED – completed / finished.</li>
 * <li>DROPPED – dropped / abandoned; will not be continued.</li>
 * </ul>
 */
public enum LibraryEntryStatus {
    PLANNED,
    IN_PROGRESS,
    COMPLETED,
    DROPPED
}