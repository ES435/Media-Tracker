type MediaCardProps = {
    title: string;
    cover: string;
    url?: string;
};

function MediaCard({ title, cover, url }: MediaCardProps) {
    return (
        <a
            href={url}
            target="_blank"
            style={{
                width: "180px",
                borderRadius: "12px",
                overflow: "hidden",
                background: "#fff",
                boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
                textDecoration: "none",
                color: "inherit"
            }}
        >
            <img
                src={cover}
                alt={title}
                style={{ width: "100%", height: "250px", objectFit: "cover" }}
            />
            <div style={{ padding: "10px", fontWeight: "bold" }}>
                {title}
            </div>
        </a>
    );
}

export default MediaCard;