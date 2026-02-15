export type MediaItem = {
    type: string;
    id?: string;
    externalId?: string;
    mediaId?: string;
    title: string;
    imageUrl: string;
    sourceUrl: string;

    /**
     * Optionale Metadaten abhängig vom Medientyp.
     * Eigenschaften sind optional, um Laufzeitfehler zu vermeiden,
     * falls das Backend bestimmte Felder nicht bereitstellt.
     */

    meta?: {
        year?: number;
        episodes?: number;
    };
};

/**
 * Repräsentiert ein öffentliches Benutzerprofil.
 */
export type User = {
    username: string;
    profilePictureUrl: string;
    publicList: boolean;
};

/**
 * Repräsentiert einen Medieneintrag,
 * der in der persönlichen Bibliothek eines Nutzers gespeichert ist.
 */
export type UserMedia = {
    id: string;
    userId: string;
    status: MediaStatus;
    rating?: number;
    notes: string;
    createdAt: string;
    updatedAt: string;
    mediaItem: MediaItem;
};

/**
 * Antwortstruktur für den Endpunkt der Benutzerprofil-Seite.
 */
export type UserPageResponse = {
    user: User;
    userMediaList: UserMedia[];
};

/**
 * Unterstützte Medienkategorien für Suche und Filterung.
 * Als Union-Typ definiert, um ungültige Werte zu verhindern.
 */
export type MediaType =
    | "anime"
    | "book"
    | "game"
    | "manga"
    | "movie"
    | "music"
    | "series"
    | "";

/**
 * Mögliche Statuswerte für Medieneinträge
 * in der Bibliothek eines Nutzers.
 */
export type MediaStatus =
    | "PLANNED"
    | "IN_PROGRESS"
    | "COMPLETED"
    | "DROPPED";

/**
 * Status-Filteroptionen für Benutzer-Medienansichten.
 */
export type UserMediaStatus =
    | "COMPLETED"
    | "IN_PROGRESS"
    | "PLANNED"
    | "DROPPED";

/**
 * Sortier- und Filteroptionen für Medienlisten eines Nutzers.
 */
export type UserMediaSortOption =
    | "anime"
    | "book"
    | "game"
    | "manga"
    | "movie"
    | "music"
    | "series"
    | "all";

export type UserMediaStatusFilter = UserMediaStatus | "ALL";
