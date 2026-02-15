import { useEffect, useState } from "react";
import Navbar from "../components/Navbar.tsx";
import Content from "../components/Content.tsx";
import Footer from "../components/Footer.tsx";
import type {MediaItem, MediaType} from "../components/types.ts";
import {useAuth} from "../service/AuthContext.tsx";

/**
 * MainPage handles page-level state and coordinates search functionality.
 * It connects UI components with backend communication.
 */

export default function MainPage() {
    // Stores search results returned from backend
    const [items, setItems] = useState<MediaItem[]>([]);

    // Controls loading indicator during async operations
    const [loading, setLoading] = useState(true);

    // Current search input value
    const [query, setQuery] = useState("");

    // Selected media filter type
    const [selectedType, setSelectedType] = useState<MediaType>("anime");

    // Optional backend limit parameter
    const limit = "";

    // Retrieves authenticated user for Navbar display
    const {user: loggedInUser} = useAuth();

    /**
     * Applies page-specific styling by modifying body classes.
     * Ensures proper layout separation between login and main view.
     */
    useEffect(() => {
        document.body.classList.add("main-page");
        document.body.classList.remove("login-page");
        return () => document.body.classList.remove("main-page");
    }, []);

    /**
     * Executes a search request to the backend API.
     *
     * @param query - User input search string.
     * @param type - Selected media type filter.
     *
     * Handles loading state and error fallback.
     */

    async function search(query: string, type: MediaType) {
        try {
            setLoading(true);

            // Encode parameters to avoid malformed URLs
            const url = `http://localhost:8080/api/search?q=${encodeURIComponent(query)}&types=${encodeURIComponent(type)}&limit=${encodeURIComponent(limit)}`;

            const response = await fetch(url, {
                credentials:"include"
            });
            if (!response.ok) {
                if (response.status === 401 || response.status === 403) {
                }
                throw new Error(`HTTP ${response.status}`);
            }

            const data: MediaItem[] = await response.json();
            setItems(data);
        } catch (err) {
            console.error(err);
            setItems([]);
        } finally {
            setLoading(false);
        }
    }
    /**
     * Triggers an initial search on component mount.
     * Designed to load default results.
     */
    useEffect(() => {
        search(query, selectedType);
    }, []);

    /**
     * Triggers a new search after validating user input.
     * Prevents empty or whitespace-only queries.
     */

    function handleSearch() {
        const trimmed = query.trim();
        if (!trimmed) return;

        search(trimmed, selectedType);
    }

    /**
     * Updates the selected media type and
     * optionally triggers a new search if a query exists.
     */

    function handleTypeChange(newType: MediaType) {
        setSelectedType(newType);
        const trimmed = query.trim();
        if (!trimmed) return;
        search(trimmed, newType);
    }

    return (
        <div className="grid-container">
            <header id="header">
                <h1 className="title">Media-Tracker 3</h1>
            </header>
            <Navbar
                query={query}
                onQueryChange={setQuery}
                onSearch={handleSearch}
                username={loggedInUser?.username ?? undefined}
                profilePictureUrl={loggedInUser?.profilePictureUrl ?? undefined}
            />
            <aside id="aside">
                <h2 className="friend-title">Friends</h2>
                <button className="friend-button">Your imaginary Friend 1</button>
                <button className="friend-button">Your imaginary Friend 2</button>
                <button className="friend-button">Larry</button>
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
