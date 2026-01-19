import { useNavigate } from "react-router-dom";
import type {FormEvent} from "react";

export default function LoginPage() {
    const navigate = useNavigate(); // Hook für die Navigation

    async function handleSubmit(event:FormEvent<HTMLFormElement>) {
        event.preventDefault();

        const formData = new FormData(event.currentTarget);
        const username = formData.get("username") as string;
        const password = formData.get("password") as string;

        login(username, password)
            .then(() => navigate("/main"))
            .catch(() => alert("Login fehlgeschlagen!"));
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
                <button type="submit" className="btn">Login</button>
                <div className="register-link">
                    <p>Don't have an account? <a href="#">Register</a></p>
                </div>
            </form>
        </div>
    );
}

async function login(username: string, password: string) {
    const url = "http://localhost:8080/auth/login"

    const response = await fetch(url, {
        method: "POST",
        credentials: "include",
        headers: {
            'content-type': 'application/json'
        },
        body: JSON.stringify({username, password})
    });

    if (!response.ok) {
        throw new Error("HTTP Error " + response.status);
    }

    return true;
}