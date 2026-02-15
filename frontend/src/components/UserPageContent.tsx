import { useMemo, useState } from "react";
import MediaCard from "./MediaCard";
import type { UserMedia, UserMediaSortOption, UserMediaStatus } from "./types";

/**
 * UserPageContent-Komponente.
 *
 * Verantwortlichkeit:
 * - Rendert die Medienbibliothek des Nutzers als Grid aus MediaCards.
 * - Bietet client-seitige Filterung nach Medientyp, Status und Titelsuche.
 * - Verwaltet lokalen UI-Auswahlzustand für aufgeklappte Karten.
 *
 * Architektonische Rolle:
 * - Komponente der Präsentationsschicht.
 * - Erhält Daten und Filter-State über Props.
 * - Führt In-Memory-Filterung aus (keine Backend-Kommunikation).
 */

type Props = {
    items: UserMedia[];
    searchQuery: string;
    selectedSortType: UserMediaSortOption;
    selectedMediaStatus: UserMediaStatus | "ALL";
    onSortTypeChange: (v: UserMediaSortOption) => void;
    onMediaStatusChange: (v: UserMediaStatus | "ALL") => void;
};

export default function UserPageContent({
                                            items,
                                            searchQuery,
                                            selectedSortType,
                                            selectedMediaStatus,
                                            onSortTypeChange,
                                            onMediaStatusChange,
                                        }: Props) {

    // Verfolgt, welcher Bibliotheks-Eintrag aktuell ausgewählt/aufgeklappt ist
    const [selectedId, setSelectedId] = useState<string | null>(null);


    /**
     * Berechnet die gefilterte Liste der Nutzer-Medieneinträge basierend auf:
     * - ausgewähltem Medientyp (Sort-/Typ-Filter)
     * - ausgewähltem Status-Filter
     * - Suchanfrage (Abgleich mit dem Medientitel)
     *
     * Memoisiert, um die Filter nicht bei jedem Re-Render neu zu berechnen,
     * wenn sich die Abhängigkeiten nicht verändert haben.
     */
    const filtered = useMemo(() => {
        const q = searchQuery.trim().toLowerCase();

        return items.filter((entry) => {
            const typeOk =
                selectedSortType === "all" || entry.mediaItem?.type === selectedSortType;

            const statusOk =
                selectedMediaStatus === "ALL" || entry.status === selectedMediaStatus;

            const titleOk =
                q.length === 0 ||
                (entry.mediaItem?.title ?? "").toLowerCase().includes(q);

            return typeOk && statusOk && titleOk;
        });
    }, [items, searchQuery, selectedSortType, selectedMediaStatus]);

    return (
        <>
            <div className="sort-options">
                {/* Medientyp-Filter */}
                <select
                    className="sort-button"
                    value={selectedSortType}
                    onChange={(e) => onSortTypeChange(e.target.value as UserMediaSortOption)}
                >
                    <option value="all">All</option>
                    <option value="anime">Anime</option>
                    <option value="book">Book</option>
                    <option value="game">Game</option>
                    <option value="manga">Manga</option>
                    <option value="movie">Movie</option>
                    <option value="music">Music</option>
                    <option value="series">Series</option>
                </select>

                {/* Status filter */}
                <select
                    className="sort-button"
                    value={selectedMediaStatus}
                    onChange={(e) => onMediaStatusChange(e.target.value as UserMediaStatus | "ALL")}
                >
                    <option value="ALL">All statuses</option>
                    <option value="COMPLETED">Completed</option>
                    <option value="IN_PROGRESS">In Progress</option>
                    <option value="PLANNED">Planned</option>
                    <option value="DROPPED">Dropped</option>
                </select>
            </div>

            // Empty-State-Feedback, wenn keine Einträge zu den aktuellen Filtern passen
            {filtered.length === 0 ? (
                <div className="content-loading">
                    No entries match your filters.
                </div>
            ) : (
                <div className="media-grid">
                    {filtered.map((entry) => (
                        <MediaCard
                            key={entry.id}
                            item={entry.mediaItem}
                            selected={selectedId === entry.id}
                            onSelect={() =>
                                setSelectedId((prev) => (prev === entry.id ? null : entry.id))
                            }
                        />
                    ))}
                </div>
            )}
        </>
    );
}
