import {type FormEvent, useEffect, useState} from "react";
import {Link, useNavigate} from "react-router-dom";

/**
 * RegisterPage component.
 *
 * Responsibility:
 * - Provides user registration form.
 * - Performs basic client-side validation.
 * - Sends registration request to backend.
 * - Redirects to login page upon successful registration.
 *
 * Architectural Role:
 * - Presentation + authentication interaction layer.
 * - Delegates persistence and user creation to backend.
 */

export default function RegisterPage() {
    // Router navigation hook
    const navigate = useNavigate()

    // Stores error messages returned from validation or backend
    const [error, setError] = useState<string | null>(null);

    /**
     * Applies login-style layout to registration page
     * to ensure consistent authentication UI styling.
     */
    useEffect(() => {
        document.body.classList.add("login-page");
        return () => {document.body.classList.remove("login-page");
        };
    }, []);

    /**
     * Handles form submission and triggers registration process.
     */
    async function handleSubmit(event:FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setError(null)

        const formData = new FormData(event.currentTarget);
        const username = formData.get("username") as string;
        const password = formData.get("password") as string;
        const passwordrep = formData.get("passwordrep") as string;

        try {
            await register(username, password, passwordrep)
            navigate("/login")
        } catch (err) {
            // FIX: Proper error checking instead of ts-ignore
            if (err instanceof Error) {
                setError(err.message)
            } else {
                setError("An unknown error occurred")
            }
        }
    }

    /**
     * Sends registration request to backend authentication endpoint.
     *
     * Performs client-side password validation before sending request.
     *
     * @throws Error when:
     * - Passwords do not match
     * - Username is already taken (409)
     * - Server returns generic failure
     */
    async function register(username: string, password: string, passwordRep: string) {
        if(!validatePassword(password, passwordRep)) {
            throw new Error("Passwords don't match.")
        }

        const url = "http://localhost:8080/auth/register"
        const response = await fetch(url, {
            method: "POST",
            credentials: "include",
            headers: {
                'content-type': 'application/json'
            },
            body: JSON.stringify({username, password, passwordRep})
        })

        if (!response.ok) {
            if(response.status === 409) {
                throw new Error("Username already taken.")
            }
            throw new Error("An error occurred. Please try again later.");
        }
    }

    /**
     * Simple client-side validation to ensure
     * password confirmation matches.
     */
    function validatePassword(password: string, passwordrep: string) {
        return password === passwordrep;
    }

    return (
        <div className="wrapper">
            <form onSubmit={handleSubmit}>
                <h1>Register</h1>
                <div className="input-box">
                    <input name="username" type="text" placeholder="Username" required/>
                </div>
                <div className="input-box">
                    <input name="password" type="password" placeholder="Password" required/>
                </div>
                <div className="input-box">
                    <input name="passwordrep" type="password" placeholder="Repeat Password" required/>
                </div>
                {/* Displays validation or backend error messages */}
                {error && <div className="error-message">{error}</div>}
                <button type="submit" className="btn">Register</button>
                <div className="register-link">
                    <p>Have an account already? <Link to="/login">Login</Link></p>
                </div>
            </form>
        </div>
    )
}