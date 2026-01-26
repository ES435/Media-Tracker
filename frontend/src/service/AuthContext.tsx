import { createContext, useContext, useState, useEffect, useCallback } from "react";
import type { ReactNode } from "react";
import type { User } from "../components/types.ts";

interface AuthContextType {
    user: (User & { userid: string }) | null;
    setUser: (user: (User & { userid: string }) | null) => void;
    logout: () => void;
    fetchWithRefresh: (url: string, options?: RequestInit) => Promise<Response>;
    isLoading: boolean;
    refreshUser: () => Promise<void>; // Neue Funktion zum Aktualisieren
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<(User & { userid: string }) | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    let refreshPromise: Promise<void> | null = null;

    const fetchWithRefresh = async (url: string, options: RequestInit = {}) => {
        let response = await fetch(url, {
            ...options,
            credentials: "include",
        });

        if (response.status === 401) {
            try {
                await refreshToken();
                response = await fetch(url, { ...options, credentials: "include" });
            } catch (exception) {
                await logout();
                throw new Error("Session expired");
            }
        }

        return response;
    };

    const refreshUser = useCallback(async () => {
        try {
            const response = await fetchWithRefresh("http://localhost:8080/auth/me");
            if (response.ok) {
                const data = await response.json();
                setUser(data);
            } else {
                setUser(null);
            }
        } catch (err) {
            setUser(null);
        } finally {
            setIsLoading(false);
        }
    }, []);

    useEffect(() => {
        refreshUser();
    }, [refreshUser]);

    const logout = async () => {
        setUser(null);
        const url = "http://localhost:8080/auth/logout";
        try {
            await fetch(url, {
                method: "POST",
                credentials: "include"
            });
        } catch (e) {
            console.error("Logout failed", e);
        }
    };

    async function refreshToken() {
        if (refreshPromise) return refreshPromise;

        refreshPromise = (async () => {
            const res = await fetch('http://localhost:8080/auth/refresh', {
                method: 'POST',
                credentials: 'include'
            });

            if (!res.ok) {
                await logout();
                throw new Error('Refresh failed');
            }
            refreshPromise = null;
        })();

        return refreshPromise;
    }

    return (
        <AuthContext.Provider value={{ user, setUser, logout, isLoading, fetchWithRefresh, refreshUser }}>
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