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

export type MediaType =
    | "anime"
    | "book"
    | "game"
    | "manga"
    | "movie"
    | "music"
    | "series"
    | "";

