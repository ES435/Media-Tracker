import type { MediaItem } from "./types";
import { useState } from "react";
import type {MediaStatus} from "./types";
import {useAuth} from "../service/AuthContext.tsx";

/**
 * MediaCard component.
 *
 * Responsibility:
 * - Displays a single media item.
 * - Allows users to add the item to their personal library.
 * - Manages local UI state (status, rating, notes, saving state).
 *
 * Architectural Role:
 * - Presentation component with localized business interaction.
 * - Uses AuthContext abstraction for authenticated backend requests.
 * - Does not manage global state.
 */

type MediaCardProps = {
    item: MediaItem;
    selected: boolean;
    onSelect: () => void;
};

export default function MediaCard({ item, selected, onSelect }: MediaCardProps){

    // Local UI state for form input and interaction feedback
    const [status, setStatus] = useState<MediaStatus>("PLANNED");
    const [rating, setRating] = useState<number | "">("");
    const [notes, setNotes] = useState("");
    const [saving, setSaving] = useState(false);
    const [added, setAdded] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // Fallback to default image if no valid imageUrl is provided
    const coverSrc = item?.imageUrl?.trim() ? item.imageUrl : "/assets/profile-picture.png"; // anderes

    // Access authenticated request wrapper from AuthContext
    const { fetchWithRefresh } = useAuth();

    /**
     * Sends the current media item to the backend library endpoint.
     *
     * Handles:
     * - Authenticated request via fetchWithRefresh
     * - HTTP status interpretation (401, 403, 409)
     * - Error propagation to UI
     */

    async function addToLibrary() {
        // Ensures a consistent ID mapping from various backend formats
        const searchResultPayload = {
            ...item,
            // Resolved: removed 'as any' casts because types.ts now supports these fields
            id: item.id || item.mediaId || item.externalId
        };

        const payload = {
            status,
            notes,
            searchResult: searchResultPayload,
        };

        const res = await fetchWithRefresh("http://localhost:8080/api/library", {
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
                {/* Media cover and optional source link */}
                <div className="media-card__main">
                    <img src={coverSrc} alt={item?.title ?? "Media"} className="media-card__img"/>

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

                {/* Interaction panel for adding to personal library */}
                <div className="media-card__side" onClick={(e) => e.stopPropagation()}>
                    {/* HEADER */}
                    <div className="media-card__panelHeader">
                        <h2 className="media-card__panelTitle">Add to Library</h2>
                        <div className="media-card__divider" />
                    </div>

                    {/* BODY */}
                    <div className="media-card__panelBody">

                        {/* Media status selection */}
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

                        {/* Optional rating input */}
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

                        {/* Optional notes field */}
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

                        {/* Displays backend-related errors */}
                        {error && <div className="media-card__error">{error}</div>}
                    </div>

                    {/* Cancels selection without triggering parent click */}
                    <div className="media-card__actions">
                        <button
                            type="button"
                            className="media-card__btn media-card__btn--ghost"
                            onClick={(e) => {
                                e.stopPropagation();
                                onSelect();
                            }}
                            disabled={saving}
                        >
                            Cancel
                        </button>

                        {/* Executes async save operation with feedback state */}
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
                                } catch (err: unknown) {
                                    if (err instanceof Error) {
                                        setError(err.message);
                                    } else {
                                        setError("An unknown error occurred");
                                    }
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

            {/* Bottom title area */}
            <div className="media-card__title" title={item.title}>
                {item.title}
            </div>
        </div>
    );
}