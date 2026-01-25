import { useEffect, useState, } from "react";
import { useParams} from "react-router-dom";
import Navbar from "../components/Navbar.tsx";
import UserPageContent from "../components/UserPageContent.tsx";
import Footer from "../components/Footer.tsx";
import type { UserMediaSortOption, UserMediaStatus, UserPageResponse } from "../components/types.ts";
import defaultAvatar from "../assets/profile-picture.png";

export default function UserPage() {
    const {username} = useParams<{ username: string }>();
    const [user, setUser] = useState<UserPageResponse["user"] | null>(null);
    const [mediaList, setMediaList] = useState<UserPageResponse["userMediaList"]>([]);
    const [loading, setLoading] = useState(true);
    const [query, setQuery] = useState("");
    const [selectedSortType, setSelectedSortType] = useState<UserMediaSortOption>("all");
    const [selectedMediaStatus, setSelectedMediaStatus] = useState<UserMediaStatus | "ALL">("ALL");
    const profileSrc = user?.profilePictureUrl?.trim() ? user.profilePictureUrl : defaultAvatar;


    //fetch UserData from username
    useEffect(() => {
        if (!username) return;
        setLoading(true);
        fetch(`http://localhost:8080/api/user/${encodeURIComponent(username)}`)
            .then(res => res.json())
            .then((data: UserPageResponse) => {
                //console.log("Fetched data: ", data)
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

    function handleSortTypeChange(newOption: UserMediaSortOption): void {
        setSelectedSortType(newOption);
    }

    function handleMediaStatusChange(newStatus: UserMediaStatus | "ALL"): void {
        setSelectedMediaStatus(newStatus);
    }


    return (
        <div className="UserPage">
            {loading ? (
                <div>Loading...</div>
            ) : user ? (
                <div className="grid-container">
                    <header id="header">
                        <div id="profile-pic-wrapper">
                            <img id="profile-picture" src={profileSrc} alt={`${user.username}'s profile`}/>
                        </div>
                        <h1 className="title">{user.username}'s Media List</h1>
                    </header>

                    <nav id="navbar">
                        <Navbar
                            query={query}
                            onQueryChange={setQuery}
                            onSearch={() => {
                            }}
                            username="snobbo"                   //HIER USER ERGÄNZEN!!!!!!!!!!!!!
                            profilePictureUrl={null}
                        />
                    </nav>

                    <aside id="aside">
                        {/* friends */}
                    </aside>

                    <main id="content">
                        <UserPageContent
                            items={mediaList}
                            searchQuery={query}
                            selectedSortType={selectedSortType}
                            selectedMediaStatus={selectedMediaStatus}
                            onSortTypeChange={handleSortTypeChange}
                            onMediaStatusChange={handleMediaStatusChange}
                        />
                    </main>

                    <footer id="footer">
                        <Footer/>
                    </footer>
                </div>
            ) : (
                <div>User not found.</div>
            )}
        </div>
    );
}