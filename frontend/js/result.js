const API_BASE = "http://localhost:8080";

function headers() {
    const h = { "X-Session-Id": localStorage.getItem("sessionId") || "" };
    const userId = localStorage.getItem("userId");
    if (userId) h["X-User-Id"] = userId;
    return h;
}

function pills(list, cls) {
    if (!list.length) return `<span class="muted">None</span>`;
    const tagClass = cls === "matched" ? "tag tag-success" : "tag tag-danger";
    return list.map((s) => `<span class="${tagClass}">${s}</span>`).join("");
}

function scoreTone(score) {
    if (score >= 80) return "#16A34A";
    if (score >= 50) return "#D97706";
    return "#DC2626";
}

function animateScore(target, element) {
    let value = 0;
    const step = Math.max(1, Math.round(target / 28));
    const timer = setInterval(() => {
        value += step;
        if (value >= target) {
            value = target;
            clearInterval(timer);
        }
        element.textContent = `${value}%`;
    }, 28);
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

    const tone = scoreTone(data.fitScore);
    document.getElementById("resultContainer").innerHTML = `
        <div class="result-layout">
            <section class="card">
                <div class="score-circle" id="scoreCircle" style="border-color:${tone};">
                    <div class="score-label">Fit Score</div>
                    <div class="score-value" id="scoreValue" style="color:${tone};">0%</div>
                </div>
            </section>
            <section class="card">
                <h3 class="section-title">Matched Skills</h3>
                <div class="tag-row">${pills(data.matchedSkills, "matched")}</div>
                <h3 class="section-title" style="margin-top:18px;">Missing Skills</h3>
                <div class="tag-row">${pills(data.missingSkills, "missing")}</div>
                <h3 class="section-title" style="margin-top:18px;">Suggestions</h3>
                <div class="suggestion-box">${data.suggestions}</div>
            </section>
        </div>
    `;
    animateScore(data.fitScore, document.getElementById("scoreValue"));
}

loadResult().catch((e) => {
    document.getElementById("error").textContent = e.message;
});
