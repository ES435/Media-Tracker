import { useNavigate } from "react-router-dom";
import defaultAvatar from "../assets/profile-picture.png";


type NavbarProps = {
    query: string;
    onQueryChange: (value: string) => void;
    onSearch: () => void;

    // NEU: statischer user (später Context)
    username?: string;
    profilePictureUrl?: string | null;
};

export default function Navbar({query, onQueryChange, onSearch, username, profilePictureUrl, }: NavbarProps) {
    const navigate = useNavigate();
    const DEFAULT_AVATAR = defaultAvatar;
    const avatarSrc =
        profilePictureUrl && profilePictureUrl.trim().length > 0
            ? profilePictureUrl
            : DEFAULT_AVATAR;

    return (
        <nav id="navbar">
            <div className="search">
                <span className="search-icon material-symbols-outlined">search</span>
                <input
                    className="search-input"
                    type="search"
                    placeholder="Search For The Meaning Of Life"
                    value={query}
                    onChange={(e) => onQueryChange(e.target.value)}
                    onKeyDown={(e) => {
                        if (e.key === "Enter") onSearch();
                    }}
                />
            </div>

            <button
                type="button"
                className="profile-button"
                onClick={() => {
                    if (!username) navigate("/login");
                    else navigate(`/user/${encodeURIComponent(username)}`);
                }}
            >
                <img
                    className="profile-button__avatar"
                    src={avatarSrc}
                    alt=""
                    onError={(e) => {
                        (e.currentTarget as HTMLImageElement).src = DEFAULT_AVATAR;
                    }}
                />
                <span className="profile-button__label">{username ?? "User"}</span>
            </button>

        </nav>
    );
}