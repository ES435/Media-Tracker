import { useEffect, useState, } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar.tsx";
import UserPageContent from "../components/UserPageContent.tsx";
import Footer from "../components/Footer.tsx";
import type { UserPageResponse } from "../components/types.ts";

export default function UserPage() {
    const { username } = useParams<{ username: string }>();
    const [user, setUser] = useState<UserPageResponse["user"] | null>(null);
    const [mediaList, setMediaList] = useState<UserPageResponse["userMediaList"]>([]);
    const [loading, setLoading] = useState(true);
    const [query, setQuery] = useState("");
    const navigate = useNavigate();

    const limit = "";
    
    //fetch UserData from username
    useEffect(() => {
        if (!username) return;
        setLoading(true);
        fetch(`http://localhost:8080/api/user/${encodeURIComponent(username)}`)
            .then(res => res.json())
            .then((data: UserPageResponse) => {
                console.log("Fetched data: ", data)
                setUser(data.user);
                setMediaList(data.userMediaList)
        })
            .catch(err => {
                console.error(err);
                setUser(null);
                setMediaList([]);
            })
            .finally(() => setLoading(false));
    }, [username]);

    const handleSearch = (query: string) => {
        navigate("/", { state: { query } });
    };

    return (
    <>
        {loading ? (
            <div>Loading...</div>
        ) : user != null ? (
            <div className="grid-container">
                <header id="header">
                <div id="user-page-navbar">
                    <Navbar query={query} onQueryChange={setQuery} onSearch={() => handleSearch(query)} showFilter={false} />
                </div>
                <div id="profile-pic-wrapper">
                    <img id="profile-picture" src={user.profilePictureUrl} alt={`${user.username}'s profile`} />
                </div>
                <h1 className="title">{username}'s Media List</h1>
                </header>

            <aside id="aside">
                <button className="add-friend-button">Add to Friendlist</button>
                <br />
                <h2 className="friend-title">Friends</h2>
                <button className="friend-button">Option 1</button>
                <button className="friend-button">Option 2</button>
                <button className="friend-button">Option 3</button>
            </aside>
            <UserPageContent
                items={mediaList}
                loading={loading}
                //selectedType={selectedType}
                //onTypeChange={handleTypeChange}
            />
            
            <Footer />
            </div>
        ) : (
            <div>User not found.</div>
        )}
    </>
    );
}