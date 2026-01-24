type MediaCardProps = {
    title: string;
    cover: string;
    url?: string;
    selected: boolean;
    onSelect: () => void;
};

function MediaCard({ title, cover, url, selected, onSelect }: MediaCardProps) {
    return (
        <div
            className={`media-card ${selected ? "selected" : ""}`}
            onClick={onSelect}
        >
            <div className="media-card__top">
                <img
                    src={cover}
                    alt={title}
                    className="media-card__img"
                />

                <div className="media-card__side">
                    <button
                        type="button"
                        onClick={(e) => e.stopPropagation()}
                    >
                        Add to list
                    </button>

                    {url && (
                        <a
                            href={url}
                            target="_blank"
                            rel="noreferrer"
                            onClick={(e) => e.stopPropagation()}
                        >
                            Source
                        </a>
                    )}
                </div>
            </div>

            <div className="media-card__title">
                {title}
            </div>
        </div>
    );
}

export default MediaCard;