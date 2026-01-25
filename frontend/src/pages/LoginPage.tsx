import {Link, useNavigate} from "react-router-dom";
import {type FormEvent, useState} from "react";
import {useAuth} from "../service/AuthContext.tsx";

export default function LoginPage() {
    const navigate = useNavigate();
    const [error, setError] = useState<string | null>(null);
    const {setUser} = useAuth();

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

        setUser(await response.json());

        return true;
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