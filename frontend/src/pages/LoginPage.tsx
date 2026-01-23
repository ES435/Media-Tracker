import { useNavigate } from "react-router-dom";
import {type FormEvent, useState} from "react";
import {login} from "../service/authService.ts";

export default function LoginPage() {
    const navigate = useNavigate();
    const [error, setError] = useState<string | null>(null);

    async function handleSubmit(event:FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setError(null)

        const formData = new FormData(event.currentTarget);
        const username = formData.get("username") as string;
        const password = formData.get("password") as string;

        try {
            await login(username, password)
            navigate("/main")
        } catch (err) {
            // @ts-ignore
            setError(err.message)
        }
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
                <button type="submit" className="btn">Login</button>
                <div className="register-link">
                    <p>Don't have an account? <a href="/register">Register</a></p>
                </div>
            </form>
        </div>
    );
}