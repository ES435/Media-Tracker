import { createContext, useContext, useState, useEffect} from "react";
import type {ReactNode} from "react";
import type {User} from "../components/types.ts";
import Cookies from 'js-cookie';

interface AuthContextType {
    user: (User & { userid: string }) | null;
    setUser: (user: (User & { userid: string }) | null) => void;
    logout: () => void;
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

    useEffect(() => {
        const savedUser = getMetadataFromCookie();
        if (savedUser) {
            setUser(savedUser);
        }
        setIsLoading(false);
    }, []);

    const logout = async () => {
        const url = "http://localhost:8080/auth/logout"

        const response = await fetch(url, {
            method: "POST",
            credentials: "include"
        })

        if (!response.ok) {
            throw new Error("HTTP Error " + response.status)
        }

        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, setUser, logout, isLoading }}>
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