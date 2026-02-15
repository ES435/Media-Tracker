import { createContext, useContext, useState, useEffect, useCallback } from "react";
import type { ReactNode } from "react";
import type { User } from "../components/types.ts";

/**
 * AuthContext / AuthProvider.
 *
 * Provides global authentication state and helper functions for the frontend.
 * Centralizes session handling (current user, logout) and offers a fetch wrapper
 * that retries requests after refreshing an expired session.
 *
 * Architectural Role:
 * - Shared application service accessible from any component via `useAuth()`.
 * - Prevents duplicated auth/session logic across pages and UI components.
 */

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
    // Holds authenticated user data (null if not logged in)
    const [user, setUser] = useState<(User & { userid: string }) | null>(null);

    // Indicates whether the initial authentication check is still running
    const [isLoading, setIsLoading] = useState(true);

    // Shared promise to avoid issuing multiple refresh requests concurrently
    let refreshPromise: Promise<void> | null = null;

    /**
     * Wraps `fetch` to ensure requests include cookies and automatically
     * retry once after a session refresh if the backend returns HTTP 401.
     *
     * @param url - Target URL (backend endpoint)
     * @param options - Standard fetch options (method, headers, body, ...)
     * @returns The final fetch Response (either initial or retried)
     * @throws Error when refresh fails and the session is considered expired
     */

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

    /**
     * Loads the currently authenticated user from the backend (`/auth/me`).
     * Sets `user` accordingly and finishes the initial loading phase.
     */

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

    /**
     * Runs the initial authentication check once on provider mount.
     */

    useEffect(() => {
        refreshUser();
    }, [refreshUser]);

    /**
     * Logs the user out locally and informs the backend to invalidate the session.
     * After logout, `user` is always set to null.
     */

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

    /**
     * Requests a refreshed session/token from the backend (`/auth/refresh`).
     * Uses a shared promise to prevent parallel refresh calls.
     *
     * @returns A promise that resolves when refresh succeeds
     * @throws Error when refresh fails
     */

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

/**
 * React hook to access authentication state and helpers.
 *
 * @throws Error if used outside of an AuthProvider
 */

export function useAuth() {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return context;
}