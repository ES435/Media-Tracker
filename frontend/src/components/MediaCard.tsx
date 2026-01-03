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
                width: "250px",
                borderRadius: "12px",
                overflow: "hidden",
                background: "#202020",
                boxShadow: "0 4px 12px rgba(192,128,64,8)",
                textDecoration: "none",
                color: "white",
            }}
        >
            <img
                src={cover}
                alt={title}
                style={{ width: "100%", height: "325px", objectFit: "cover" }}
            />
            <div style={{ padding: "10px", fontFamily: "Consolas, monospace"}}>
                {title}
            </div>
        </a>
    );
}

export default MediaCard;