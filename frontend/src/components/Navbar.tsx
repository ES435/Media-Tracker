import { useNavigate } from "react-router-dom";
import defaultAvatar from "../assets/profile-picture.png";

/**
 * Navbar-Komponente.
 *
 * Verantwortlichkeit:
 * - Stellt die Suchfeld-Funktionalität bereit.
 * - Zeigt Informationen zum aktuellen Nutzer an.
 * - Behandelt die Navigation zum Login oder zur Nutzerprofil-Seite.
 *
 * Architektonische Rolle:
 * - Komponente der Präsentationsschicht.
 * - Erhält State und Callbacks über Props.
 * - Verwaltet den Authentifizierungsstatus nicht direkt.
 */

type NavbarProps = {
    query: string;
    onQueryChange: (value: string) => void;
    onSearch: () => void;

    // Optionale Nutzerdaten für die Profilanzeige
    username?: string;
    profilePictureUrl?: string | null;
};

export default function Navbar({query, onQueryChange, onSearch, username, profilePictureUrl, }: NavbarProps) {

    // React-Router-Navigations-Hook
    const navigate = useNavigate();

    // Fallback-Avatarbild für fehlende oder ungültige Profilbilder
    const DEFAULT_AVATAR = defaultAvatar;
    const avatarSrc =
        profilePictureUrl && profilePictureUrl.trim().length > 0
            ? profilePictureUrl
            : DEFAULT_AVATAR;

    return (
        <nav id="navbar">
            {/* Navbar-Bereich */}
            <div className="search">
                <span className="search-icon material-symbols-outlined">search</span>
                <input
                    className="search-input"
                    type="search"
                    placeholder="Search For The Meaning Of Life"
                    value={query}
                    onChange={(e) => onQueryChange(e.target.value)}
                    onKeyDown={(e) => {
                        // Ermöglicht das Ausführen der Suche per Enter-Taste
                        if (e.key === "Enter") onSearch();
                    }}
                />
            </div>

            {/* Profile navigation button */}
            <button
                type="button"
                className="profile-button"
                // Leitet nicht eingeloggte Nutzer zum Login weiter
                // Eingeloggte Nutzer zu ihrer Profilseite
                onClick={() => {
                    if (!username) navigate("/login");
                    else navigate(`/user/${encodeURIComponent(username)}`);
                }}
            >
                <img
                    className="profile-button__avatar"
                    src={avatarSrc}
                    alt=""
                    // Stellt den Fallback-Avatar sicher, falls das Laden des Bildes fehlschlägt
                    onError={(e) => {
                        (e.currentTarget as HTMLImageElement).src = DEFAULT_AVATAR;
                    }}
                />
                <span className="profile-button__label">{username ?? "User"}</span>
            </button>

        </nav>
    );
}
