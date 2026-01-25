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
            return
        }
    }

    if (!response.ok) throw new Error('Request failed.');
    return await response;
}

