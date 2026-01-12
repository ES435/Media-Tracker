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
            rel="noreferrer"
            className="media-card"
        >
            <img
                src={cover}
                alt={title}
                className="media-card__img"
            />
            <div className="media-card__title">
                {title}
            </div>
        </a>
    );
}

export default MediaCard;