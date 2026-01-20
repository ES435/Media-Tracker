import {useNavigate} from "react-router-dom";
import {type FormEvent, useState} from "react";

export default function RegisterPage() {
    const navigate = useNavigate()
    const [error, setError] = useState<string | null>(null);

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
            // @ts-ignore
            setError(err.message)
        }
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
                    <p>Have an account already? <a href="/login">Login</a></p>
                </div>
            </form>
        </div>
    )
}

async function register(username: string, password: string, passwordrep: string) {
    if(!validatePassword(password, passwordrep)) {
        throw new Error("Passwords don't match.")
    }

    const url = "http://localhost:8080/auth/register"
    const response = await fetch(url, {
        method: "POST",
        credentials: "include",
        headers: {
            'content-type': 'application/json'
        },
        body: JSON.stringify({username, password})
    })

    if (!response.ok) {
        if(response.status === 401) {
            throw new Error("Username already taken.")
        }
        throw new Error("HTTP Error " + response.status);
    }
}

function validatePassword(password: string, passwordrep: string) {
    return password === passwordrep;
}