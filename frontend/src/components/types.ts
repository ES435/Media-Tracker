export type MediaItem = {
    type: string;
    id: string;
    title: string;
    imageUrl: string;
    sourceUrl: string;
    meta?: {
        year?: number; //"?" damit nichts crashed, sollte es keinen return der api geben
        episodes?: number;
    };
};

export type User = {
    username: string;
    profilePictureUrl: string;
    publicList: boolean;
};

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

export type UserPageResponse = {
    user: User;
    userMediaList: UserMedia[];
};

export type MediaType =
    | "anime"
    | "book"
    | "game"
    | "manga"
    | "movie"
    | "music"
    | "series"
    | "";

export type MediaStatus =
    | "PLANNED"
    | "IN_PROGRESS"
    | "COMPLETED"
    | "DROPPED";
export type UserMediaStatus =
    | "COMPLETED"
    | "IN_PROGRESS"
    | "PLANNED"
    | "DROPPED";

export type UserMediaSortOption =
    | "anime"
    | "book"
    | "game"
    | "manga"
    | "movie"
    | "music"
    | "series"
    | "all";

