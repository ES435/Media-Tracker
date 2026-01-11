import { useEffect, useState } from "react";
import Header from "../components/Header.tsx";
import Navbar from "../components/Navbar.tsx";
import Aside from "../components/Aside.tsx";
import Content from "../components/Content.tsx";
import Footer from "../components/Footer.tsx";
import type {MediaItem, MediaType} from "../components/types.ts";


export default function MainPage() {
    const [items, setItems] = useState<MediaItem[]>([]);
    const [loading, setLoading] = useState(true);
    const [query, setQuery] = useState("");
    const [selectedType, setSelectedType] = useState<MediaType>("anime");

    const limit = "";

    async function search(q: string, type: MediaType) {
        try {
            setLoading(true);

            const url = `http://localhost:8080/api/search?q=${encodeURIComponent(q)}&types=${encodeURIComponent(type)}&limit=${encodeURIComponent(limit)}`;

            const res = await fetch(url);
            if (!res.ok) throw new Error(`HTTP ${res.status}`);

            const data: MediaItem[] = await res.json();
            setItems(data);
        } catch (err) {
            console.error(err);
            setItems([]);
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        search(query, selectedType);
    }, []);

    function handleSearch() {
        const trimmed = query.trim();
        if (!trimmed) return;

        search(trimmed, selectedType);
    }

    function handleTypeChange(newType: MediaType) {
        setSelectedType(newType);
        const trimmed = query.trim();
        if (!trimmed) return;
        search(trimmed, newType);
    }

    return (
        <div className="grid-container">
            <Header/>
            <Navbar query={query} onQueryChange={setQuery} onSearch={handleSearch} />
            <Aside />
            <Content
                items={items}
                loading={loading}
                selectedType={selectedType}
                onTypeChange={handleTypeChange}
            />
            <Footer />
        </div>
    );
}
