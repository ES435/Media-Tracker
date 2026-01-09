
type NavbarProps = {
    query: string;
    onQueryChange: (value: string) => void;
    onSearch: () => void;
};

export default function Navbar({ query, onQueryChange, onSearch }: NavbarProps) {
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
                        if (e.key === "Enter") {
                            onSearch();
                        }
                    }}
                />
            </div>
            <button className="login_button">Login Button UwU</button>
        </nav>
    );
}