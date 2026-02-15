import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "bootstrap/dist/css/bootstrap.css";
import "./index.css";
import "./pages/LoginPage.css";
import "./pages/UserPage.css";

import App from "./App.tsx";

/**
 * Application entry point.
 *
 * Responsibility:
 * - Bootstraps the React application.
 * - Injects global styles.
 * - Mounts the root App component into the DOM.
 *
 * Architectural Role:
 * - Defines the rendering root.
 * - Applies global CSS (Bootstrap + custom styles).
 * - Wraps application in React StrictMode for development checks.
 *
 * Initializes React rendering process
 * and attaches App component to #root element.
 */

createRoot(document.getElementById("root")!).render(
    <StrictMode>
        <App />
    </StrictMode>
);
