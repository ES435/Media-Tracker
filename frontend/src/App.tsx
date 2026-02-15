import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage.tsx";
import MainPage from "./pages/MainPage.tsx";
import RegisterPage from "./pages/RegisterPage.tsx";
import UserPage from "./pages/UserPage.tsx"
import {AuthProvider} from "./service/AuthContext.tsx";

/**
 * Root-Anwendungskomponente.
 *
 * Verantwortlichkeit:
 * - Definiert die globale Routing-Konfiguration.
 * - Umhüllt die Anwendung mit dem AuthProvider, um den Authentifizierungs-Context bereitzustellen.
 * - Legt die Navigationsstruktur für alle Seiten fest.
 *
 * Architektonische Rolle:
 * - Composition Root der Frontend-Anwendung.
 * - Zentralisiert Route-Definitionen.
 * - Injiziert globale Services (AuthContext).
 */

export default function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>
                    {/* Leitet den Root-Pfad zum Login weiter */}
                    <Route path="/" element={<Navigate to="/login"/>}/>
                    {/* Authentifizierungs-Routen */}
                    <Route path="/login" element={<LoginPage/>}/>
                    {/* Hauptanwendungs-Routen */}
                    <Route path="/main" element={<MainPage/>}/>
                    <Route path="/register" element={<RegisterPage/>}/>
                    {/* Fallback für unbekannte Routen */}
                    <Route path="*" element={<h1>404 - Not Found</h1>}/>
                    <Route path="/user/:username" element={<UserPage/>}/>
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}
