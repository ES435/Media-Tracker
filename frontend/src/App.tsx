import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage.tsx";
import MainPage from "./pages/MainPage.tsx"; 

export default function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Navigate to="/login" />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/main" element={<MainPage />} />
                <Route path="*" element={<h1>404 - Not Found</h1>} />
            </Routes>
        </BrowserRouter>
    );
}