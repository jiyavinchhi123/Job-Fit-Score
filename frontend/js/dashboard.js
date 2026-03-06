const API_BASE = "http://localhost:8080";

function getHeaders() {
    const headers = { "X-Session-Id": localStorage.getItem("sessionId") || "" };
    const userId = localStorage.getItem("userId");
    if (userId) headers["X-User-Id"] = userId;
    return headers;
}

function scoreClass(score) {
    if (score >= 80) return "score-chip score-good";
    if (score >= 50) return "score-chip score-mid";
    return "score-chip score-low";
}

function tableRows(items) {
    return items.map((item, idx) => `
        <tr>
            <td>${new Date().toLocaleDateString()}</td>
            <td><span class="${scoreClass(item.fitScore)}">${item.fitScore}%</span></td>
            <td>Resume Scan #${item.resultId || idx + 1}</td>
        </tr>
    `).join("");
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
        document.getElementById("totalScans").textContent = "0";
        document.getElementById("avgScore").textContent = "0%";
        document.getElementById("bestScore").textContent = "0%";
        return;
    }

    const scores = data.map((x) => Number(x.fitScore || 0));
    const total = scores.length;
    const avg = Math.round(scores.reduce((a, b) => a + b, 0) / total);
    const best = Math.max(...scores);
    document.getElementById("totalScans").textContent = String(total);
    document.getElementById("avgScore").textContent = `${avg}%`;
    document.getElementById("bestScore").textContent = `${best}%`;

    history.innerHTML = `
        <div class="table-wrap">
            <table class="history-table">
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Score</th>
                        <th>Job Title</th>
                    </tr>
                </thead>
                <tbody>${tableRows(data)}</tbody>
            </table>
        </div>
    `;
}

loadHistory().catch((e) => {
    document.getElementById("history").innerHTML = `<p class="error">${e.message}</p>`;
});
