import MediaCard from "./MediaCard";
import type { MediaItem, MediaType } from "./types";
import {useState} from "react";

export default function Content({items, loading, selectedType, onTypeChange,}: {
    items: MediaItem[];
    loading: boolean;
    selectedType: MediaType;
    onTypeChange: (value: MediaType) => void;

})
{
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

                    <button className="create-button">Manual Entry</button>
                </div>

                {loading ? (
                    <div className="content-loading">Loading...</div>
                ) : (
                    items.map((item) => (
                        <MediaCard
                            key={item.id}
                            title={item.title}
                            cover={item.imageUrl}
                            url={item.sourceUrl}
                            selected={item.id === selectedId}
                            onSelect={() =>
                                setSelectedId(
                                    selectedId === item.id ? null : item.id
                                )
                            }
                        />
                    ))
                )}
            </div>
        </main>
    );
}
