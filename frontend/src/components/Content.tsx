import MediaCard from "./MediaCard";
import type { MediaItem, MediaType } from "./types";
import {useState} from "react";

/**
 * Content-Komponente.
 *
 * Verantwortlichkeit:
 * - Rendert den Hauptinhalt der Seite.
 * - Zeigt Suchergebnisse als Grid aus MediaCard-Komponenten an.
 * - Verwaltet lokalen UI-Auswahlzustand für aufgeklappte Karten.
 *
 * Architektonische Rolle:
 * - Reine Präsentationskomponente.
 * - Erhält Daten und Callbacks über Props.
 * - Führt selbst keine Backend-Kommunikation aus.
 */

export default function Content({items, loading, selectedType, onTypeChange,}: {
    items: MediaItem[];
    loading: boolean;
    selectedType: MediaType;
    onTypeChange: (value: MediaType) => void;

})
{
    // Verfolgt, welches Medien-Item aktuell ausgewählt/aufgeklappt ist
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

                    {/* Platzhalter-Button für zukünftige Feature-Erweiterung */}
                    <button className="create-button">Under Construction</button>
                </div>

                {loading ? (
                    // Zeigt den Ladezustand während der asynchronen Suche an
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
