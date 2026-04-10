from flask import Flask, request, jsonify
import os
import re

app = Flask(__name__)

# =========================
# HEALTH CHECK (UptimeRobot)
# =========================
@app.route("/health", methods=["GET"])
def health():
    return "AI Service is running", 200


# =========================
# SKILLS DATABASE
# =========================
SKILLS = [
    "Java",
    "Python",
    "C++",
    "JavaScript",
    "SQL",
    "HTML",
    "CSS",
    "React",
    "Node",
    "Spring",
    "Django",
    "Machine Learning",
    "Data Structures",
    "Git",
    "Linux",
    "Docker",
]


# =========================
# SKILL EXTRACTION LOGIC
# =========================
def extract_skills(text):
    found = set()
    lowered = text.lower()

    for skill in SKILLS:
        pattern = r"\b" + re.escape(skill.lower()) + r"\b"
        if re.search(pattern, lowered):
            found.add(skill)

    return found


# =========================
# MAIN AI ANALYSIS API
# =========================
@app.route("/analyze", methods=["POST"])
def analyze():
    data = request.get_json(silent=True) or {}

    resume_text = data.get("resume_text", "")
    job_description = data.get("job_description", "")

    # Validate input
    if not resume_text.strip() or not job_description.strip():
        return jsonify({
            "error": "resume_text and job_description are required"
        }), 400

    # Extract skills
    resume_skills = extract_skills(resume_text)
    required_skills = extract_skills(job_description)

    # Calculate fit score
    if not required_skills:
        fit_score = 0
        matched_skills = []
        missing_skills = []
    else:
        matched_skills = sorted(list(resume_skills & required_skills))
        missing_skills = sorted(list(required_skills - resume_skills))
        fit_score = round((len(matched_skills) / len(required_skills)) * 100)

    # Suggestions
    if missing_skills:
        suggestions = "Add projects or experience using: " + ", ".join(missing_skills[:5]) + "."
    else:
        suggestions = "Great match. Highlight measurable outcomes for these skills."

    return jsonify({
        "fit_score": fit_score,
        "matched_skills": matched_skills,
        "missing_skills": missing_skills,
        "suggestions": suggestions
    })


# =========================
# RUN SERVER (RENDER SAFE)
# =========================
if __name__ == "__main__":
    port = int(os.environ.get("PORT", 5000))
    app.run(host="0.0.0.0", port=port, debug=False)
