import { type FormEvent, useEffect, useState } from "react";
import {Link, useNavigate} from "react-router-dom";
import {useAuth} from "../service/AuthContext.tsx";

/**
 * LoginPage component.
 *
 * Responsibility:
 * - Provides authentication form for user login.
 * - Sends credentials to backend and handles login response.
 * - Updates global authentication state via AuthContext.
 *
 * Architectural Role:
 * - Presentation + authentication interaction layer.
 * - Delegates session management to AuthContext.
 * - Performs navigation after successful login.
 */

export default function LoginPage() {
    // Router navigation hook
    const navigate = useNavigate();
    // Holds error messages returned from login attempt
    const [error, setError] = useState<string | null>(null);

    /**
     * Applies login-specific body styling.
     * Ensures layout separation between login and main application view.
     */
    useEffect(() => {
        document.body.classList.add("login-page");
        document.body.classList.remove("main-page");
        return () => document.body.classList.remove("login-page");
    }, []);

    // Retrieves refreshUser function to update global auth state
    const {refreshUser} = useAuth();

    /**
     * Handles form submission event.
     * Extracts form data and triggers login process.
     */
    async function handleSubmit(event:FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setError(null)

        const formData = new FormData(event.currentTarget);
        const username = formData.get("username") as string;
        const password = formData.get("password") as string;
        const isRememberMe = formData.get("remember-me") === "on";

        try {
            await login(username, password, isRememberMe)
            navigate("/main")
        } catch (err) {
            // @ts-ignore
            setError(err.message)
        }
    }

    /**
     * Sends login request to backend authentication endpoint.
     *
     * @param username - User login identifier
     * @param password - User password
     * @param rememberMe - Indicates whether persistent login is requested
     *
     * On success:
     * - Refreshes authenticated user state
     * - Returns true
     *
     * Throws error for:
     * - Invalid credentials (401)
     * - Generic server failures
     */

    async function login(username: string, password: string, rememberMe: boolean) {
        const url = "http://localhost:8080/auth/login"

        const response = await fetch(url, {
            method: "POST",
            credentials: "include",
            headers: {
                'content-type': 'application/json'
            },
            body: JSON.stringify({username, password, rememberMe})
        });

        if (!response.ok) {
            if(response.status === 401) {
                throw new Error("Incorrect username or password")
            }

            throw new Error("An error occurred. Please try again later.");
        }

        // Synchronizes global authentication state after successful login
        await refreshUser()
        return true
    }

    return (
        <div className="wrapper">
            <form onSubmit={handleSubmit}>
                <h1>Login</h1>
                <div className="input-box">
                    <input name="username" type="text" placeholder="Username" required/>
                </div>
                <div className="input-box">
                    <input name="password" type="password" placeholder="Password" required/>
                </div>
                {/* Displays backend validation errors */}
                {error && <div className="error-message">{error}</div>}
                <label>
                    <input type="checkbox" id="remember-me" name="remember-me" />
                    Remember Me
                </label>
                <button type="submit" className="btn">Login</button>
                <div className="register-link">
                    <p>Don't have an account? <Link to={"/register"}>Register</Link></p>
                </div>
            </form>
        </div>
    );
}