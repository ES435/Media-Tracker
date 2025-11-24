package app.mediatracker.db.domain;

/**
 * Status eines Bibliothekseintrags aus Sicht des Users.
 * <ul>
 *   <li>PLANNED – geplant, zu konsumieren/lesen/spielen.</li>
 *   <li>IN_PROGRESS – aktuell in Bearbeitung.</li>
 *   <li>COMPLETED – abgeschlossen.</li>
 *   <li>DROPPED – abgebrochen, soll nicht weitergeführt werden.</li>
 * </ul>
 */
public enum LibraryEntryStatus {
    PLANNED,
    IN_PROGRESS,
    COMPLETED,
    DROPPED
}