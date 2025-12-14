import { useState, useEffect } from "react";
import MediaCard from "./components/MediaCard";
import type {MediaItem} from "./components/types";

function App() {
    const [items, setItems] = useState<MediaItem[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const q = "pancreas";   // Suchbegriff
        const types = "anime";
        const limit = 6;


        fetch(`http://localhost:8080/api/search?q=${q}&types=${types}&limit=${limit}`)
            .then(res => res.json())
            .then((data: MediaItem[]) => {
                setItems(data);
                setLoading(false);
            })
            .catch(err => {
                console.error(err);
                setLoading(false);
            });
    }, []);

    if (loading) return <p>Loading...</p>;

    return (
        <>
            <head>
                <title>Media-Tracker 3 | MT3</title>
            </head>
            <body style={{
                background: "#202020",
            }}></body>
            <h1 style={{
                color: "white",
                textShadow: "0 4px 12px rgba(192,128,64,8)",
                margin: "20px",
                fontFamily: "Consolas, monospace",
                textAlign: "center",
            }}>Media-Tracker 3</h1>

            <div style={{
                display: "grid",
                gridTemplateColumns: "repeat(auto-fill, minmax(180px, 1fr))",
                gap: "20px",
                padding: "20px"
            }}>
                {items.map((item) => (
                    <MediaCard
                        key={item.id}
                        title={item.title}
                        cover={item.imageUrl}
                        url={item.sourceUrl}
                    />
                ))}
            </div>
        </>
    );
}

export default App;
