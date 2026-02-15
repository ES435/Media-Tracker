import {type FormEvent, useEffect, useState} from "react";
import {Link, useNavigate} from "react-router-dom";

export default function RegisterPage() {
    const navigate = useNavigate()
    const [error, setError] = useState<string | null>(null);
    useEffect(() => {
        document.body.classList.add("login-page");
        return () => {document.body.classList.remove("login-page");
        };
    }, []);
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
                {error && <div className="error-message">{error}</div>}
                <button type="submit" className="btn">Register</button>
                <div className="register-link">
                    <p>Have an account already? <Link to="/login">Login</Link></p>
                </div>
            </form>
        </div>
    )
}