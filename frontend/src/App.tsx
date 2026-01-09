import { useState, useEffect } from "react";
import type {MediaItem} from "./components/types";
import Header from "./components/Header.tsx";
import Navbar from "./components/Navbar.tsx";
import Aside from "./components/Aside.tsx";
import Content from "./components/Content.tsx";
import Footer from "./components/Footer.tsx";
import './index.css';

function App() {
    const [items, setItems] = useState<MediaItem[]>([]);
    const [loading, setLoading] = useState(true);
    const [query, setQuery] = useState("");
    const types = "anime"; // default, wird noch mit filter button verbunden
    const limit = ""; // default, wird mit noch filter button verbunden

    async function search(q: string) {
        try {
            setLoading(true);

            const url = `http://localhost:8080/api/search?q=${encodeURIComponent(
                q
            )}&types=${encodeURIComponent(types)}&limit=${encodeURIComponent(limit)}`;

            const res = await fetch(url);
            if (!res.ok) {
                throw new Error(`HTTP ${res.status}`);
            }

            const data: MediaItem[] = await res.json();
            setItems(data);
        } catch (err) {
            console.error("Search failed:", err);
            setItems([]);
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        search(query);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    function handleSearch() {
        const trimmed = query.trim();
        if (!trimmed) return; // bei leerer Eingabe keine ausführung

        search(trimmed);
    }

    return (
        <div className="grid-container">
            <Header/>
            <Navbar
                query={query}
                onQueryChange={setQuery}
                onSearch={handleSearch}
            />

            <Aside />
            <Content items={items} loading={loading} />
            <Footer />
        </div>
    );
}

export default App;
