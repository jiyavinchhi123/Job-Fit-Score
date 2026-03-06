const API_BASE = "http://localhost:8080";

function headers() {
    const h = { "X-Session-Id": localStorage.getItem("sessionId") || "" };
    const userId = localStorage.getItem("userId");
    if (userId) h["X-User-Id"] = userId;
    return h;
}

function pills(list, cls) {
    if (!list.length) return `<span class="muted">None</span>`;
    return list.map((s) => `<span class="pill ${cls}">${s}</span>`).join("");
}

async function loadResult() {
    const resultId = localStorage.getItem("lastResultId");
    if (!resultId) {
        document.getElementById("error").textContent = "No result found. Run a scan first.";
        return;
    }

    const res = await fetch(`${API_BASE}/scan-result/${resultId}`, { headers: headers() });
    const data = await res.json();
    if (!res.ok) throw new Error(data.error || "Failed to load result");

    document.getElementById("resultContainer").innerHTML = `
        <div class="score">${data.fitScore}/100</div>
        <h3>Matched Skills</h3>
        <div>${pills(data.matchedSkills, "matched")}</div>
        <h3>Missing Skills</h3>
        <div>${pills(data.missingSkills, "missing")}</div>
        <h3>Suggestions</h3>
        <p>${data.suggestions}</p>
    `;
}

loadResult().catch((e) => {
    document.getElementById("error").textContent = e.message;
});
