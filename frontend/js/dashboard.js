const API_BASE = "http://localhost:8080";

function getHeaders() {
    const headers = { "X-Session-Id": localStorage.getItem("sessionId") || "" };
    const userId = localStorage.getItem("userId");
    if (userId) headers["X-User-Id"] = userId;
    return headers;
}

function row(item) {
    return `
        <div class="card" style="margin: 10px 0;">
            <div><strong>Score:</strong> ${item.fitScore}/100</div>
            <div><strong>Matched:</strong> ${item.matchedSkills.join(", ") || "None"}</div>
            <div><strong>Missing:</strong> ${item.missingSkills.join(", ") || "None"}</div>
            <div><strong>Suggestion:</strong> ${item.suggestions}</div>
        </div>
    `;
}

async function loadHistory() {
    document.getElementById("planInfo").textContent =
        `Current Plan: ${(localStorage.getItem("plan") || "free").toUpperCase()}`;

    const res = await fetch(`${API_BASE}/dashboard/history`, { headers: getHeaders() });
    const data = await res.json();
    if (!res.ok) throw new Error(data.error || "Failed to load history");

    const history = document.getElementById("history");
    if (!data.length) {
        history.innerHTML = "<p class='muted'>No scans yet.</p>";
        return;
    }
    history.innerHTML = data.map(row).join("");
}

loadHistory().catch((e) => {
    document.getElementById("history").innerHTML = `<p class="error">${e.message}</p>`;
});
