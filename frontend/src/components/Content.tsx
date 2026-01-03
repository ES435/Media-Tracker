import MediaCard from "./MediaCard";
import type { MediaItem } from "./types";

export default function Content({items, loading,}: {
        items: MediaItem[];
        loading: boolean;
    }) {
        if (loading) return <p>Loading...</p>;

        return (
            <main id="content">
            <>
                <div className="media-grid">
                    {items.map(item => (
                        <MediaCard
                            key={item.id}
                            title={item.title}
                            cover={item.imageUrl}
                            url={item.sourceUrl}
                        />
                    ))}
                </div>
            </>
            </main>
        );

}
