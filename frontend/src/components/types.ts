export type MediaItem = {
    type: string;
    id?: string;
    externalId?: string;
    title: string;
    imageUrl: string;
    sourceUrl: string;

    /**
     * Optional metadata depending on media type.
     * Properties are optional to avoid runtime failures
     * when backend does not provide specific fields.
     */

    meta?: {
        year?: number;
        episodes?: number;
    };
};

/**
 * Represents a public user profile.
 */
export type User = {
    username: string;
    profilePictureUrl: string;
    publicList: boolean;
};

/**
 * Represents a media entry stored in a user's personal library.
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
 * Response structure for the user profile page endpoint.
 */
export type UserPageResponse = {
    user: User;
    userMediaList: UserMedia[];
};

/**
 * Supported media categories for search and filtering.
 * Defined as union type to restrict invalid values.
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
 * Possible status values for media items in a user's library.
 */
export type MediaStatus =
    | "PLANNED"
    | "IN_PROGRESS"
    | "COMPLETED"
    | "DROPPED";

/**
 * Status filter options used in user media views.
 */
export type UserMediaStatus =
    | "COMPLETED"
    | "IN_PROGRESS"
    | "PLANNED"
    | "DROPPED";

/**
 * Sorting/filtering options for user media lists.
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
