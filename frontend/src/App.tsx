import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage.tsx";
import MainPage from "./pages/MainPage.tsx";
import RegisterPage from "./pages/RegisterPage.tsx";
import UserPage from "./pages/UserPage.tsx"
import {AuthProvider} from "./service/AuthContext.tsx";

/**
 * Root application component.
 *
 * Responsibility:
 * - Defines global routing configuration.
 * - Wraps the application with AuthProvider to provide authentication context.
 * - Establishes navigation structure for all pages.
 *
 * Architectural Role:
 * - Composition root of the frontend application.
 * - Centralizes route definitions.
 * - Injects global services (AuthContext).
 */

export default function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>
                    {/* Redirect root path to login */}
                    <Route path="/" element={<Navigate to="/login"/>}/>
                    {/* Authentication routes */}
                    <Route path="/login" element={<LoginPage/>}/>
                    {/* Main application routes */}
                    <Route path="/main" element={<MainPage/>}/>
                    <Route path="/register" element={<RegisterPage/>}/>
                    {/* Fallback for unknown routes */}
                    <Route path="*" element={<h1>404 - Not Found</h1>}/>
                    <Route path="/user/:username" element={<UserPage/>}/>
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}