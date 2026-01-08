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
                    <div className="own-column">
                        <select className="sort-button">
                            <option selected>Filter 1</option> // Default-Auswahl
                            <option>Filter 2</option>
                            <option>Filter 3</option>
                        </select>
                        <select className="sort-button">
                            <option selected>All</option> // Default-Auswahl
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
