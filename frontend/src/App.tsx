import { useState, useEffect } from "react";
import type {MediaItem} from "./components/types";
import "./index.css";
import Navbar from "./components/Navbar";
import Aside from "./components/Aside";
import Content from "./components/Content";
import Footer from "./components/Footer";

function App() {
    const [items, setItems] = useState<MediaItem[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const q = "one piece";   // Suchbegriff
        const types = "anime";
        const limit = "";


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
        <div className="grid-container">
            <Navbar />
            <Aside />
            <Content items={items} loading={loading} />
            <Footer />
        </div>
    );

}

export default App;
