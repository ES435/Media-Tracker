import { BrowserRouter, Routes, Route } from "react-router-dom";
import MainPage from "./pages/MainPage";
//import LoginPage from "./pages/LoginPage.tsx";
import UserPage from "./pages/UserPage.tsx"

export default function App() {
    //return <LoginPage />;
    //return <MainPage />;
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<MainPage />} />
                <Route path="/user/:username" element={<UserPage />} />
            </Routes>
        </BrowserRouter>
    );
}