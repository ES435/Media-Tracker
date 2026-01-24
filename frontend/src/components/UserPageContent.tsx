import MediaCard from "./MediaCard";
import type { UserMedia, UserMediaSortOption, UserMediaStatus } from "./types";

export default function UserPageContent({items, loading, selectedSortType, selectedMediaStatus, onSortTypeChange, onMediaStatusChange}: {
    items: UserMedia[];
    loading: boolean;
    selectedSortType: UserMediaSortOption;
    selectedMediaStatus: UserMediaStatus;
    onSortTypeChange: (value: UserMediaSortOption) => void;
    onMediaStatusChange: (value: UserMediaStatus) => void;
}) {
        if (loading) return <p>Loading...</p>;
        return (
            <main id="content">
                <div className="media-grid">
                    <div className="sort-options">
                            <select className="sort-button" value={selectedSortType} onChange={(event) => onSortTypeChange(event.target.value as UserMediaSortOption)}>
                                <option value="all">All</option>
                                <option value="anime">Anime</option>
                                <option value="book">Book</option>
                                <option value="game">Game</option>
                                <option value="manga">Manga</option>
                                <option value="movie">Movie</option>
                                <option value="music">Music</option>
                                <option value="series">Series</option>
                            </select>

                            <select className="sort-button" id="right-sort-button" value={selectedMediaStatus} onChange={(event) => onMediaStatusChange(event.target.value as UserMediaStatus)}>
                                <option value="COMPLETED">Completed</option>
                                <option value="IN_PROGRESS">In Progress</option>
                                <option value="PLANNED">Planned</option>
                                <option value="DROPPED">Dropped</option>
                            </select>
                    </div>
                    {
                    items.filter(item =>
                        (selectedSortType === "all" || item.mediaItem.type == selectedSortType) && item.status == selectedMediaStatus
                    ).map(item => (
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
