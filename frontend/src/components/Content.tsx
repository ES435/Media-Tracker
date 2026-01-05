import MediaCard from "./MediaCard";
import type { MediaItem } from "./types";

export default function Content({items, loading,}: {
        items: MediaItem[];
        loading: boolean;
    }) {
        if (loading) return <p>Loading...</p>;

        return (

            <main id="content">
                <div className="media-grid">
                    <h1 className="title">Media-Tracker 3</h1>
                    <div className="own-column">
                        <button className="sort-button">Order by</button>
                        <button className="sort-button">Genres</button>
                        <button className="create-button">welp</button>
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
