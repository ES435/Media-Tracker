import MediaCard from "./MediaCard";
import type { MediaItem, MediaType, UserMedia } from "./types";

export default function UserPageContent({items, loading}: {
    items: UserMedia[];
    loading: boolean;
    //selectedType: MediaType;
    //onTypeChange: (value: MediaType) => void;
}) {
        if (loading) return <p>Loading...</p>;

        return (
            <main id="content">
                <div className="media-grid">
                    <div className="own-column">
                            <select className="sort-button" //</div>value={selectedType} onChange={(e) => onTypeChange(e.target.value as MediaType)}
                            >
                                <option value="all">All</option>
                                <option value="anime">Anime</option>
                                <option value="book">Book</option>
                                <option value="game">Game</option>
                                <option value="manga">Manga</option>
                                <option value="movie">Movie</option>
                                <option value="music">Music</option>
                                <option value="series">Series</option>
                                <option value="manual-entry">Manual Entry</option>
                            </select>

                            <select className="sort-button">
                                <option selected>Completed</option>
                                <option>In Progress</option>
                                <option>Completed</option>
                                <option>Dropped</option>
                            </select>
                    </div>

                    {items.map(item => (
                        <MediaCard
                            key={item.mediaItem.id}
                            title={item.mediaItem.title}
                            cover={item.mediaItem.imageUrl}
                            url={item.mediaItem.sourceUrl}
                        />
                    ))}
                </div>
            </main>
        );
}
