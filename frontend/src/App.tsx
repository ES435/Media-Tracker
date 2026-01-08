import { useState, useEffect } from "react";
import type {MediaItem} from "./components/types";
import Navbar from "./components/Navbar.tsx";
import Aside from "./components/Aside.tsx";
import Content from "./components/Content.tsx";
import Footer from "./components/Footer.tsx";
import './index.css';

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
            <h1 className="title">Media-Tracker 3</h1>
            <Navbar/>
            <Aside/>
            <Content items={items} loading={loading}/>
            <Footer/>
        </>
    );
}

export default App;
