import { useNavigate } from "react-router-dom";
import defaultAvatar from "../assets/profile-picture.png";

/**
 * Navbar component.
 *
 * Responsibility:
 * - Provides search input functionality.
 * - Displays current user information.
 * - Handles navigation to login or user profile page.
 *
 * Architectural Role:
 * - Presentation layer component.
 * - Receives state and callbacks via props.
 * - Does not manage authentication state directly.
 */

type NavbarProps = {
    query: string;
    onQueryChange: (value: string) => void;
    onSearch: () => void;

    // Optional user data for profile display
    username?: string;
    profilePictureUrl?: string | null;
};

export default function Navbar({query, onQueryChange, onSearch, username, profilePictureUrl, }: NavbarProps) {

    // React Router navigation hook
    const navigate = useNavigate();

    // Fallback avatar image for missing or invalid profile pictures
    const DEFAULT_AVATAR = defaultAvatar;
    const avatarSrc =
        profilePictureUrl && profilePictureUrl.trim().length > 0
            ? profilePictureUrl
            : DEFAULT_AVATAR;

    return (
        <nav id="navbar">
            {/* Search input section */}
            <div className="search">
                <span className="search-icon material-symbols-outlined">search</span>
                <input
                    className="search-input"
                    type="search"
                    placeholder="Search For The Meaning Of Life"
                    value={query}
                    onChange={(e) => onQueryChange(e.target.value)}
                    onKeyDown={(e) => {
                        // Allows search execution via Enter key
                        if (e.key === "Enter") onSearch();
                    }}
                />
            </div>

            {/* Profile navigation button */}
            <button
                type="button"
                className="profile-button"
                // Redirects unauthenticated users to login
                // Authenticated users to their profile page
                onClick={() => {
                    if (!username) navigate("/login");
                    else navigate(`/user/${encodeURIComponent(username)}`);
                }}
            >
                <img
                    className="profile-button__avatar"
                    src={avatarSrc}
                    alt=""
                    // Ensures fallback avatar if image loading fails
                    onError={(e) => {
                        (e.currentTarget as HTMLImageElement).src = DEFAULT_AVATAR;
                    }}
                />
                <span className="profile-button__label">{username ?? "User"}</span>
            </button>

        </nav>
    );
}