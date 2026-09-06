const SCOK = (() => {
    function saveSession(auth) {
        localStorage.setItem("accessToken", auth.accessToken);
        localStorage.setItem("refreshToken", auth.refreshToken);
        localStorage.setItem("userId", auth.userId);
        localStorage.setItem("username", auth.username);
    }

    function clearSession() {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        localStorage.removeItem("userId");
        localStorage.removeItem("username");
    }

    function requireSession() {
        if (!localStorage.getItem("accessToken")) {
            location.replace("/login.html");
        }
    }

    async function readResponse(response) {
        if (response.status === 204) return null;

        const text = await response.text();
        if (!text) return null;

        try {
            return JSON.parse(text);
        } catch {
            return text;
        }
    }

    async function refreshAccessToken() {
        const refreshToken = localStorage.getItem("refreshToken");
        if (!refreshToken) return false;

        const response = await fetch("/api/auth/refresh", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({refreshToken})
        });

        if (!response.ok) return false;
        saveSession(await response.json());
        return true;
    }

    async function api(path, options = {}, retry = true) {
        const headers = new Headers(options.headers || {});
        headers.set("Authorization", `Bearer ${localStorage.getItem("accessToken")}`);
        if (options.body) headers.set("Content-Type", "application/json");

        const response = await fetch(path, {...options, headers});

        if (response.status === 401 && retry && await refreshAccessToken()) {
            return api(path, options, false);
        }

        const body = await readResponse(response);
        if (!response.ok) {
            const message = body?.message || body?.error || `Request failed (${response.status})`;
            throw new Error(message);
        }
        return body;
    }

    async function logout() {
        try {
            await api("/api/auth/logout", {method: "POST"});
        } finally {
            clearSession();
            location.replace("/login.html");
        }
    }

    return {
        api,
        clearSession,
        logout,
        requireSession,
        saveSession,
        accessToken: () => localStorage.getItem("accessToken"),
        userId: () => localStorage.getItem("userId"),
        username: () => localStorage.getItem("username")
    };
})();
