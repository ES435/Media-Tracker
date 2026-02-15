import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "bootstrap/dist/css/bootstrap.css";
import "./index.css";
import "./pages/LoginPage.css";
import "./pages/UserPage.css";

import App from "./App.tsx";

/**
 * Einstiegspunkt der Anwendung.
 *
 * Verantwortlichkeit:
 * - Bootstrapped die React-Anwendung.
 * - Bindet globale Styles ein.
 * - Mountet die Root-App-Komponente in das DOM.
 *
 * Architektonische Rolle:
 * - Definiert die Rendering-Root.
 * - Wendet globales CSS an (Bootstrap + eigene Styles).
 * - Umhüllt die Anwendung mit React StrictMode für Entwicklungsprüfungen.
 *
 * Initialisiert den React-Rendering-Prozess
 * und hängt die App-Komponente an das #root-Element an.
 */

createRoot(document.getElementById("root")!).render(
    <StrictMode>
        <App />
    </StrictMode>
);
