import MediaCard from "./MediaCard";
import type { MediaItem, MediaType } from "./types";

export default function Content({items, loading, selectedType, onTypeChange,}: {
    items: MediaItem[];
    loading: boolean;
    selectedType: MediaType;
    onTypeChange: (value: MediaType) => void;
}) {
        if (loading) return <p>Loading...</p>;

        return (
            <main id="content">
                <div className="media-grid">
                    <div className="own-column">
                            <select className="sort-button" value={selectedType} onChange={(e) => onTypeChange(e.target.value as MediaType)}>
                                <option value="anime">Anime</option>
                                <option value="book">Book</option>
                                <option value="game">Game</option>
                                <option value="manga">Manga</option>
                                <option value="movie">Movie</option>
                                <option value="music">Music</option>
                                <option value="series">Series</option>
                            </select>

                            <select className="sort-button">
                                <option selected>All</option>
                                <option>Genre 1</option>
                                <option>Genre 2</option>
                                <option>Genre 3</option>
                            </select>
                            <button className="create-button">Cheeseburger</button>
                    </div>

                    {items.map(item => (
                        <MediaCard
                            key={item.id}
                            title={item.title}
                            cover={item.imageUrl}
                            url={item.sourceUrl}
                        />
                    ))}
                </div>
            </main>
        );
}
