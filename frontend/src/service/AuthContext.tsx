import { createContext, useContext, useState, useEffect} from "react";
import type {ReactNode} from "react";
import type {User} from "../components/types.ts";
import Cookies from 'js-cookie';

interface AuthContextType {
    user: (User) | null;
    setUser: (user: (User & { userid: string }) | null) => void;
    logout: () => void;
    fetchWithRefresh: (url: string, options?: RequestInit) => Promise<Response>;
    isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const getMetadataFromCookie = () => {
    const cookieValue = Cookies.get('user_metadata');
    if (!cookieValue) return null;

    try {
        return JSON.parse(cookieValue);
    } catch (exception) {
        return null;
    }
};

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<(User & { userid: string }) | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    let refreshPromise: Promise<void> | null = null;

    useEffect(() => {
        const savedUser = getMetadataFromCookie();
        if (savedUser) {
            setUser(savedUser);
        }
        setIsLoading(false);
    }, []);

    const logout = async () => {
        setUser(null);

        const url = "http://localhost:8080/auth/logout"

        const response = await fetch(url, {
            method: "POST",
            credentials: "include"
        })
        if (!response.ok) {
            throw new Error("HTTP Error " + response.status)
        }
    };

const fetchWithRefresh = async (url: string, options: RequestInit = {}) => {
    let response = await fetch(url, {
        ...options,
        credentials: "include",
    });

    if (response.status === 401) {
        try {
            await refreshToken()
                .then(async () => response = await fetch(url, options))
        }catch (exception) {
            await logout();
            throw new Error("Session expired");
        }
    }

    return response;
}

async function refreshToken() {
    if (refreshPromise) {
        return refreshPromise;
    }

    refreshPromise = (async () => {
        const res = await fetch('http://localhost:8080/auth/refresh', {
            method: 'POST',
            credentials: 'include'
        });

        if (!res.ok) {
            await logout();
            throw new Error('Refresh failed');
        }

        await Promise.resolve()
        refreshPromise = null;
    })()

    return refreshPromise;
}

return (
    <AuthContext.Provider value={{ user, setUser, logout, isLoading, fetchWithRefresh }}>
        {children}
    </AuthContext.Provider>
);
}

export function useAuth() {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return context;
}