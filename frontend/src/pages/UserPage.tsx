import { useEffect, useState } from "react";
import Navbar from "../components/Navbar.tsx";
import Content from "../components/Content.tsx";
import Footer from "../components/Footer.tsx";
import type {MediaItem, MediaType} from "../components/types.ts";

export default function UserPage() {
    const [items, setItems] = useState<MediaItem[]>([]);
    const [loading, setLoading] = useState(true);
    const [query, setQuery] = useState("");

    const limit = "";
    
    return (
        <>
            <Navbar />
            {}
            <Footer />
        </>
    )
}