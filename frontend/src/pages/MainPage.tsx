import { useEffect, useState } from "react";
import Navbar from "../components/Navbar.tsx";
import Content from "../components/Content.tsx";
import Footer from "../components/Footer.tsx";
import type {MediaItem, MediaType} from "../components/types.ts";
import {useNavigate} from "react-router-dom";


export default function MainPage() {
    const [items, setItems] = useState<MediaItem[]>([]);
    const [loading, setLoading] = useState(true);
    const [query, setQuery] = useState("");
    const [selectedType, setSelectedType] = useState<MediaType>("anime");

    const limit = "";

    const navigate = useNavigate();

    async function search(q: string, type: MediaType) {
        try {
            setLoading(true);

            const url = `http://localhost:8080/api/search?q=${encodeURIComponent(q)}&types=${encodeURIComponent(type)}&limit=${encodeURIComponent(limit)}`;

            const res = await fetch(url, {
                credentials:"include"
            });
            if (!res.ok) {
                if (res.status === 401 || res.status === 403) {
                    navigate("/login")
                }
                throw new Error(`HTTP ${res.status}`);
            }

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

    //Aside wird eventuell wieder ein Component, je nach Umfang
    return (
        <div className="grid-container">
            <header id="header">
                <h1 className="title">Media-Tracker 3</h1>
            </header>
            <Navbar query={query} onQueryChange={setQuery} onSearch={handleSearch} />
            <aside id="aside">
                <h2 className="friend-title">Friends</h2>
                <button className="friend-button">Option 1</button>
                <button className="friend-button">Option 2</button>
                <button className="friend-button">Option 3</button>
            </aside>
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
