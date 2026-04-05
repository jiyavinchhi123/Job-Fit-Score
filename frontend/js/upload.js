const API_BASE = (window.API_BASE || "https://jobfit-backend-gnr1.onrender.com");


function getSessionId() {
    let sessionId = localStorage.getItem("sessionId");
    if (!sessionId) {
        sessionId = crypto.randomUUID();
        localStorage.setItem("sessionId", sessionId);
    }
    return sessionId;
}

function getUserId() {
    return localStorage.getItem("userId");
}

if (!getUserId()) {
    window.location.href = "./login.html";
}

function authHeaders(extra = {}) {
    const headers = { ...extra, "X-Session-Id": getSessionId() };
    const userId = getUserId();
    if (userId) headers["X-User-Id"] = userId;
    return headers;
}

async function uploadResume(file) {
    const formData = new FormData();
    formData.append("file", file);
    const res = await fetch(`${API_BASE}/upload-resume`, {
        method: "POST",
        headers: authHeaders(),
        body: formData
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.error || "Upload failed");
    return data.resumeId;
}

async function analyzeJob(resumeId, jobDescription) {
    const res = await fetch(`${API_BASE}/analyze-job`, {
        method: "POST",
        headers: authHeaders({ "Content-Type": "application/json" }),
        body: JSON.stringify({ resumeId, jobDescription })
    });
    const data = await res.json();
    if (!res.ok) throw { status: res.status, message: data.error || "Analyze failed" };
    return data;
}

const dropzone = document.getElementById("dropzone");
const resumeInput = document.getElementById("resume");
const analyzeBtn = document.getElementById("checkFitBtn");
const messageEl = document.getElementById("message");

if (dropzone && resumeInput) {
    ["dragenter", "dragover"].forEach((eventName) => {
        dropzone.addEventListener(eventName, (e) => {
            e.preventDefault();
            dropzone.classList.add("active");
        });
    });
    ["dragleave", "drop"].forEach((eventName) => {
        dropzone.addEventListener(eventName, (e) => {
            e.preventDefault();
            dropzone.classList.remove("active");
        });
    });
    dropzone.addEventListener("drop", (e) => {
        const [file] = e.dataTransfer.files;
        if (file) {
            const dt = new DataTransfer();
            dt.items.add(file);
            resumeInput.files = dt.files;
            messageEl.className = "muted";
            messageEl.textContent = `Selected: ${file.name}`;
        }
    });
}

function setLoadingState(isLoading) {
    if (!analyzeBtn) return;
    analyzeBtn.disabled = isLoading;
    analyzeBtn.innerHTML = isLoading
        ? `<span class="spinner"></span>Analyzing...`
        : "Analyze Resume";
}

analyzeBtn?.addEventListener("click", async () => {
    messageEl.className = "muted";
    messageEl.textContent = "Processing your resume...";
    setLoadingState(true);

    try {
        const file = resumeInput.files[0];
        const jobDescription = document.getElementById("jobDescription").value.trim();
        if (!file) throw new Error("Please upload a resume file.");
        if (!jobDescription) throw new Error("Please paste a job description.");

        const resumeId = await uploadResume(file);
        const result = await analyzeJob(resumeId, jobDescription);
        localStorage.setItem("lastResultId", result.resultId);
        window.location.href = "./result.html";
    } catch (err) {
        if (err.status === 402) {
            messageEl.className = "error";
            messageEl.textContent = `${err.message} Redirecting to pricing...`;
            setTimeout(() => (window.location.href = "./pricing.html"), 900);
            return;
        }
        messageEl.className = "error";
        messageEl.textContent = err.message || "Something went wrong";
    } finally {
        setLoadingState(false);
    }
});
