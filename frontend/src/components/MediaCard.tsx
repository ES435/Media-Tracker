import type { MediaItem } from "./types";
import { useState } from "react";
import type {MediaStatus} from "./types";

type MediaCardProps = {
    item: MediaItem;
    selected: boolean;
    onSelect: () => void;
};

export default function MediaCard({ item, selected, onSelect }: MediaCardProps) {
    const [status, setStatus] = useState<MediaStatus>("PLANNED");
    const [rating, setRating] = useState<number | "">("");
    const [notes, setNotes] = useState("");
    const [saving, setSaving] = useState(false);
    const [added, setAdded] = useState(false);
    const [error, setError] = useState<string | null>(null);

    async function addToLibrary() {
        const payload: any = {
            status,
            notes,
            searchResult: item,
        };
        if (rating !== "") payload.rating = rating;

        const res = await fetch("http://localhost:8080/api/library", {
            method: "POST",
            credentials: "include",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload),
        });

        if (!res.ok) {
            const details = await res.text();
            if (res.status === 401 || res.status === 403) throw new Error("Not logged in.");
            if (res.status === 409) throw new Error("Already in your library.");
            throw new Error(`HTTP ${res.status}: ${details}`);
        }

        return await res.json();
    }

    return (
        <div className={`media-card ${selected ? "selected" : ""}`}>
            <div className="media-card__top" onClick={onSelect} style={{ cursor: "pointer" }}>
                {/* LEFT: Cover */}
                <div className="media-card__main">
                    <img src={item.imageUrl} alt={item.title} className="media-card__img" />

                    {/* optional wie Screenshot: Source Link unter dem Cover */}
                    {item.sourceUrl && (
                        <a
                            className="media-card__source"
                            href={item.sourceUrl}
                            target="_blank"
                            rel="noreferrer"
                            onClick={(e) => e.stopPropagation()}
                        >
                            Source Link
                        </a>
                    )}
                </div>

                <div className="media-card__side" onClick={(e) => e.stopPropagation()}>
                    {/* HEADER */}
                    <div className="media-card__panelHeader">
                        <h2 className="media-card__panelTitle">Add to Library</h2>
                        <div className="media-card__divider" />
                    </div>

                    {/* BODY (scrollt, wenn zu viel Inhalt) */}
                    <div className="media-card__panelBody">
                        <div className="media-card__field">
                            <label className="media-card__label">Status</label>
                            <select
                                className="media-card__control"
                                value={status}
                                onChange={(e) => setStatus(e.target.value as MediaStatus)}
                                disabled={saving || added}
                            >
                                <option value="PLANNED">Planned</option>
                                <option value="IN_PROGRESS">In Progress</option>
                                <option value="COMPLETED">Completed</option>
                                <option value="DROPPED">Dropped</option>
                            </select>
                        </div>

                        <div className="media-card__field">
                            <label className="media-card__label">Rating (1-10)</label>
                            <select
                                className="media-card__control"
                                value={rating}
                                onChange={(e) => setRating(e.target.value ? Number(e.target.value) : "")}
                                disabled={saving || added}
                            >
                                <option value="">No Rating</option>
                                {Array.from({ length: 10 }, (_, i) => i + 1).map((n) => (
                                    <option key={n} value={n}>
                                        {n}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div className="media-card__field">
                            <label className="media-card__label">Notes</label>
                            <textarea
                                className="media-card__textarea"
                                placeholder="Write your thoughts..."
                                value={notes}
                                onChange={(e) => setNotes(e.target.value)}
                                disabled={saving || added}
                            />
                        </div>

                        {error && <div className="media-card__error">{error}</div>}
                    </div>

                    {/* FOOTER (bleibt sichtbar) */}
                    <div className="media-card__actions">
                        <button
                            type="button"
                            className="media-card__btn media-card__btn--ghost"
                            onClick={(e) => {
                                e.stopPropagation();
                                onSelect(); // klappt Panel zu
                            }}
                            disabled={saving}
                        >
                            Cancel
                        </button>

                        <button
                            type="button"
                            className="media-card__btn media-card__btn--primary"
                            disabled={saving || added}
                            onClick={async (e) => {
                                e.stopPropagation();
                                setError(null);
                                setSaving(true);
                                try {
                                    await addToLibrary();
                                    setAdded(true);
                                } catch (err: any) {
                                    setError(err?.message ?? "Failed");
                                } finally {
                                    setSaving(false);
                                }
                            }}
                        >
                            {added ? "Saved ✓" : saving ? "Saving..." : "Save Entry"}
                        </button>
                    </div>
                </div>
            </div>

            {/* bottom title */}
            <div className="media-card__title" title={item.title}>
                {item.title}
            </div>
        </div>
    );
}
