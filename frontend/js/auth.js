const API_BASE = "http://localhost:8080";

function sessionId() {
    let id = localStorage.getItem("sessionId");
    if (!id) {
        id = crypto.randomUUID();
        localStorage.setItem("sessionId", id);
    }
    return id;
}

async function auth(endpoint, email, password) {
    const res = await fetch(`${API_BASE}/${endpoint}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-Session-Id": sessionId()
        },
        body: JSON.stringify({ email, password })
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.error || "Auth failed");
    localStorage.setItem("userId", data.userId);
    localStorage.setItem("plan", data.plan);
    localStorage.setItem("email", data.email);
    return data;
}

function bind(id, endpoint, emailId, pwdId) {
    document.getElementById(id).addEventListener("click", async () => {
        const email = document.getElementById(emailId).value.trim();
        const password = document.getElementById(pwdId).value;
        const msg = document.getElementById("authMessage");
        msg.className = "muted";
        msg.textContent = "Processing...";
        try {
            const data = await auth(endpoint, email, password);
            msg.className = "success";
            msg.textContent = `${data.message}. Plan: ${data.plan.toUpperCase()}`;
        } catch (e) {
            msg.className = "error";
            msg.textContent = e.message;
        }
    });
}

bind("registerBtn", "register", "regEmail", "regPassword");
bind("loginBtn", "login", "loginEmail", "loginPassword");
