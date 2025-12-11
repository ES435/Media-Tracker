export type MediaItem = {
    type: string;
    id: string;
    title: string;
    imageUrl: string;
    sourceUrl: string;
    meta: {
        year: number;
        episodes: number;
    };
};

