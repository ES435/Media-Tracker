import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage.tsx";
import MainPage from "./pages/MainPage.tsx";
import RegisterPage from "./pages/RegisterPage.tsx";
import UserPage from "./pages/UserPage.tsx"
import {AuthProvider} from "./service/AuthContext.tsx";

export default function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Navigate to="/login"/>}/>
                    <Route path="/login" element={<LoginPage/>}/>
                    <Route path="/main" element={<MainPage/>}/>
                    <Route path="/register" element={<RegisterPage/>}/>
                    <Route path="*" element={<h1>404 - Not Found</h1>}/>
                    <Route path="/user/:username" element={<UserPage/>}/>
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}