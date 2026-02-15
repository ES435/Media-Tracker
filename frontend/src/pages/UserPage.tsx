import { useEffect, useState, } from "react";
import { useParams} from "react-router-dom";
import Navbar from "../components/Navbar.tsx";
import UserPageContent from "../components/UserPageContent.tsx";
import Footer from "../components/Footer.tsx";
import type { UserMediaSortOption, UserMediaStatus, UserPageResponse } from "../components/types.ts";
import defaultAvatar from "../assets/profile-picture.png";
import {useAuth} from "../service/AuthContext.tsx";

/**
 * UserPage component.
 *
 * Responsibility:
 * - Loads and displays a public user profile and their media library.
 * - Manages page-level state for loading, filters, and search query.
 * - Coordinates child components (Navbar, UserPageContent, Footer).
 *
 * Architectural Role:
 * - Page/orchestration layer.
 * - Uses AuthContext abstraction (`fetchWithRefresh`) for authenticated requests.
 * - Delegates rendering and client-side filtering to child components.
 */

export default function UserPage() {
    // Reads the target username from the route (e.g. /user/:username)
    const {username} = useParams<{ username: string }>();
    // Profile data of the visited user
    const [user, setUser] = useState<UserPageResponse["user"] | null>(null);
    // Library entries belonging to the visited user
    const [mediaList, setMediaList] = useState<UserPageResponse["userMediaList"]>([]);
    // Controls page loading state while fetching profile data
    const [loading, setLoading] = useState(true);
    // Search query for client-side filtering in UserPageContent
    const [query, setQuery] = useState("");
    // Selected media type filter (client-side)
    const [selectedSortType, setSelectedSortType] = useState<UserMediaSortOption>("all");
    // Selected status filter (client-side)
    const [selectedMediaStatus, setSelectedMediaStatus] = useState<UserMediaStatus | "ALL">("ALL");
    // Fallback avatar image if profilePictureUrl is missing or invalid
    const profileSrc = user?.profilePictureUrl?.trim() ? user.profilePictureUrl : defaultAvatar;
    // Logged-in user is used for Navbar display (profile button)
    const { user: loggedInUser, fetchWithRefresh } = useAuth();


    /**
     * Fetches user profile data (user + media list) based on route parameter.
     * Uses AuthContext's fetch wrapper to ensure consistent session handling.
     */
    useEffect(() => {
        if (!username) return;
        setLoading(true);
        fetchWithRefresh(`http://localhost:8080/api/user/${encodeURIComponent(username)}`)
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

    /**
     * Updates media type filter selection for client-side filtering.
     */
    function handleSortTypeChange(newOption: UserMediaSortOption): void {
        setSelectedSortType(newOption);
    }

    /**
     * Updates status filter selection for client-side filtering.
     */
    function handleMediaStatusChange(newStatus: UserMediaStatus | "ALL"): void {
        setSelectedMediaStatus(newStatus);
    }


    return (
        <div className="UserPage">
            {loading ? (
                // Loading state while profile data is being fetched
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
                            // Search is applied client-side in UserPageContent
                            onSearch={() => {
                            }}
                            username={loggedInUser?.username}
                            profilePictureUrl={loggedInUser?.profilePictureUrl ?? null}
                        />
                    </nav>

                    <aside id="aside">
                        {/* friends (placeholder)*/}
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
                // Fallback view when requested user profile does not exist
                <div>User not found.</div>
            )}
        </div>
    );
}