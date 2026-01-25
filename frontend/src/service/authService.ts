let refreshPromise: Promise<void> | null = null;

export async function refreshAccessToken() {
    if (refreshPromise) {
        return refreshPromise;
    }

    refreshPromise = (async () => {
        const res = await fetch('http://localhost:8080/auth/refresh', {
            method: 'POST',
            credentials: 'include'
        });

        if (!res.ok) {
            throw new Error('Refresh failed');
        }

        await Promise.resolve()
        refreshPromise = null;
    })()

    return refreshPromise;
}

export async function fetchWithAutoRefresh(url: string, options: RequestInit = {}) {
    options.credentials = 'include'
    let response = await fetch(url, options)

    if (response.status === 401) {
        try {
            await refreshAccessToken()
                .then(async () => response = await fetch(url, options))

        } catch {
            // if(onSessionExpired) onSessionExpired()
            return
        }
    }

    if (!response.ok) throw new Error('Request failed.');
    return await response;
}

export async function login(username: string, password: string, rememberMe: boolean) {
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

        throw new Error("HTTP Error " + response.status);
    }

    return true;
}

export async function register(username: string, password: string, passwordRep: string) {
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
        if(response.status === 401) {
            throw new Error("Username already taken.")
        }
        throw new Error("HTTP Error " + response.status);
    }
}

export function validatePassword(password: string, passwordrep: string) {
    return password === passwordrep;
}

export async function logout() {
    const url = "http://localhost:8080/auth/logout"

    const response = await fetch(url, {
        method: "POST",
        credentials: "include"
    })

    if (!response.ok) {
        throw new Error("HTTP Error " + response.status)
    }

    return true;
}