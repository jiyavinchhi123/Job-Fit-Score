const API_BASE = "http://localhost:8080";

function getSessionId() {
    let sessionId = localStorage.getItem("sessionId");
    if (!sessionId) {
        sessionId = crypto.randomUUID();
        localStorage.setItem("sessionId", sessionId);
    }
    return sessionId;
}

function validEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function setError(id, message) {
    const el = document.getElementById(id);
    if (el) el.textContent = message || "";
}

function clearErrors(prefix) {
    const ids = [
        `${prefix}NameError`,
        `${prefix}EmailError`,
        `${prefix}PasswordError`,
        `${prefix}ConfirmPasswordError`,
        `${prefix}FormError`
    ];
    ids.forEach((id) => setError(id, ""));
}

function setLoading(button, isLoading, label) {
    if (!button) return;
    button.disabled = isLoading;
    button.innerHTML = isLoading ? `<span class="spinner"></span>${label}` : label;
}

async function sendAuth(endpoint, body) {
    const res = await fetch(`${API_BASE}/${endpoint}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-Session-Id": getSessionId()
        },
        body: JSON.stringify(body)
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.error || "Authentication failed");
    localStorage.setItem("userId", data.userId);
    localStorage.setItem("plan", data.plan || "free");
    localStorage.setItem("email", data.email || "");
    return data;
}

function initLogin() {
    const form = document.getElementById("loginForm");
    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        clearErrors("login");
        const button = document.getElementById("loginBtn");
        const email = document.getElementById("loginEmail").value.trim();
        const password = document.getElementById("loginPassword").value;

        let valid = true;
        if (!validEmail(email)) {
            setError("loginEmailError", "Please enter a valid email.");
            valid = false;
        }
        if (password.length < 6) {
            setError("loginPasswordError", "Password must be at least 6 characters.");
            valid = false;
        }
        if (!valid) return;

        try {
            setLoading(button, true, "Logging in...");
            await sendAuth("login", { email, password });
            window.location.href = "./dashboard.html";
        } catch (err) {
            setError("loginFormError", err.message || "Login failed.");
        } finally {
            setLoading(button, false, "Login");
        }
    });
}

function initRegister() {
    const form = document.getElementById("registerForm");
    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        clearErrors("register");
        const button = document.getElementById("registerBtn");
        const name = document.getElementById("registerName").value.trim();
        const email = document.getElementById("registerEmail").value.trim();
        const password = document.getElementById("registerPassword").value;
        const confirmPassword = document.getElementById("registerConfirmPassword").value;

        let valid = true;
        if (!name) {
            setError("registerNameError", "Full name is required.");
            valid = false;
        }
        if (!validEmail(email)) {
            setError("registerEmailError", "Please enter a valid email.");
            valid = false;
        }
        if (password.length < 6) {
            setError("registerPasswordError", "Password must be at least 6 characters.");
            valid = false;
        }
        if (confirmPassword !== password) {
            setError("registerConfirmPasswordError", "Passwords do not match.");
            valid = false;
        }
        if (!valid) return;

        try {
            setLoading(button, true, "Creating account...");
            await sendAuth("register", { name, email, password });
            window.location.href = "./dashboard.html";
        } catch (err) {
            setError("registerFormError", err.message || "Register failed.");
        } finally {
            setLoading(button, false, "Create Account");
        }
    });
}

initLogin();
initRegister();
