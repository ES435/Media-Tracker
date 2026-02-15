import MediaCard from "./MediaCard";
import type { MediaItem, MediaType } from "./types";
import {useState} from "react";

/**
 * Content component.
 *
 * Responsibility:
 * - Renders the main content area of the page.
 * - Displays search results as a grid of MediaCard components.
 * - Manages local UI selection state for expanded cards.
 *
 * Architectural Role:
 * - Pure presentation component.
 * - Receives data and callbacks via props.
 * - Does not perform backend communication itself.
 */

export default function Content({items, loading, selectedType, onTypeChange,}: {
    items: MediaItem[];
    loading: boolean;
    selectedType: MediaType;
    onTypeChange: (value: MediaType) => void;

})
{
    // Tracks which media item is currently selected/expanded
    const [selectedId, setSelectedId] = useState<string | null>(null);
    return (
        <main id="content">
            <div className="media-grid">
                <div className="own-column">
                    <select
                        className="sort-button" value={selectedType} onChange={(e) => onTypeChange(e.target.value as MediaType)}>
                        <option value="anime">Anime</option>
                        <option value="book">Book</option>
                        <option value="game">Game</option>
                        <option value="manga">Manga</option>
                        <option value="movie">Movie</option>
                        <option value="music">Music</option>
                        <option value="series">Series</option>
                    </select>

                    {/* Placeholder button for future feature extension */}
                    <button className="create-button">Under Construction</button>
                </div>

                {loading ? (
                    // Displays loading state during async search execution
                    <div className="content-loading">Loading...</div>
                ) : (
                    items.map((item) => {
                        const id = item.id ?? "";
                        if (!id) return null;

                        return (
                            <MediaCard
                                key={id}
                                item={item}
                                selected={id === selectedId}
                                onSelect={() => setSelectedId((prev) => (prev === id ? null : id))}
                            />
                        );
                    })

                )}
            </div>
        </main>
    );
}
