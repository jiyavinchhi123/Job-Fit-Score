# Resume -> Job Fit Score

Micro-SaaS hackathon project with architecture:

`Frontend (HTML/CSS/JS) -> Spring Boot API -> Flask AI Service -> Supabase (PostgreSQL)`

## 1. Folder Structure

```text
/frontend
  index.html
  upload.html
  result.html
  pricing.html
  login.html
  register.html
  dashboard.html
  components.css
  dashboard.css
  auth.css
  style.css
  auth.js
  upload.js
  result.js
  /js
    components.js
    upload.js
    result.js
    auth.js
    pricing.js
    dashboard.js

/backend-java
  pom.xml
  /src/main/java/com/hackamind/jobfit/...
  /src/main/resources/application.properties

/ai-service
  app.py
  requirements.txt

/database
  schema.sql
```

## 2. Dependencies & Prerequisites

- OS: Windows 10/11 (PowerShell commands below are Windows-friendly)
- Java: 17+
- Maven: 3.9+
- Python: 3.10+
- pip: latest recommended
- Supabase Postgres: running (cloud or local)
- Browser: latest Chrome/Edge/Firefox

### Quick Version Check

```powershell
java -version
mvn -version
python --version
pip --version
```

## 3. Installation & Setup

### 3.1 Connect Supabase (PostgreSQL)

1. Resume your Supabase project (if paused).
2. Go to **Project Settings → Database → Connection string**.
3. Use the Session Pooler connection string (PostgreSQL + SSL).

### 3.2 Create Database Schema

Use Supabase SQL Editor (recommended) or `psql`:

```sql
\i d:/My projects/Hackamind/database/schema_postgres.sql
```

### 3.3 Configure Backend DB (if needed)

Default config is already set in:

`backend-java/src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:postgresql://<SUPABASE_HOST>:5432/postgres?sslmode=require
spring.datasource.username=postgres
spring.datasource.password=<YOUR_DB_PASSWORD>
```

Use your Supabase credentials. You can also set these via environment variables in production.

### 3.4 Install & Run AI Service (Flask)

```powershell
cd c:\Users\jiyav\Desktop\Hackamind\ai-service
python -m venv .venv
.\.venv\Scripts\activate
pip install -r requirements.txt
python app.py
```

Expected:
- Service on `http://127.0.0.1:5001`
- Endpoint: `POST /analyze`

### 3.5 Run Java Backend (Spring Boot)

```powershell
cd c:\Users\jiyav\Desktop\Hackamind\backend-java
mvn spring-boot:run
```

Expected:
- API on `http://localhost:8080`

### 3.6 Run Frontend

```powershell
cd c:\Users\jiyav\Desktop\Hackamind\frontend
python -m http.server 5500
```

Open:
- `http://localhost:5500`

## 4. Execution Order (Important)

Run in this order:

1. Supabase (PostgreSQL)
2. Flask AI service (`5001`)
3. Spring Boot backend (`8080`)
4. Frontend static server (`5500`)

If Flask is not running, backend analysis will fail with connection-refused for `/analyze`.

## 5. End-to-End User Flow

1. Open landing page.
2. Click `Analyze Resume`.
3. Upload PDF/DOCX resume and paste JD.
4. Submit analysis.
5. View fit score + matched/missing skills + suggestions.
6. Free plan allows 3 scans.
7. On limit exceed, go to pricing and upgrade with Razorpay test flow.
8. Login/Register to persist and view dashboard history.

## 6. Manual Test Checklist

### A. AI service health
```powershell
Invoke-RestMethod -Uri "http://127.0.0.1:5001/analyze" -Method POST -ContentType "application/json" -Body '{"resume_text":"Java SQL","job_description":"Java SQL Docker"}'
```
Expected: JSON with `fit_score`, `matched_skills`, `missing_skills`, `suggestions`.

### B. Backend health
Open browser:
- `http://localhost:8080/me` (or use Postman/curl with headers)

### C. Frontend flow
- Upload valid PDF/DOCX <= 5MB
- Run 1st to 3rd scans (allowed)
- 4th scan returns paywall behavior
- Login + Register forms validate inputs
- Dashboard shows scan entries

## 7. Security & Validation Implemented

- Resume file type validation: PDF/DOCX only.
- File size limit: 5MB.
- Password hashing with BCrypt.
- SQL injection resistance via Spring Data JPA parameterized statements.

## 8. API Documentation

See:
- `backend-java/API.md`

## 9. Troubleshooting

### `No plugin found for prefix 'spring-boot'`
- Run Maven from project root:
```powershell
cd c:\Users\jiyav\Desktop\Hackamind\backend-java
mvn spring-boot:run
```

### `I/O error ... localhost:5001/analyze Connection refused`
- Flask service is not running.
- Start `ai-service/app.py` first.

### Supabase authentication/connection error
- Ensure your Supabase project is running (not paused).
- Verify host/user/password in `application.properties` or env vars.

### CORS or frontend API errors
- Ensure backend is running on `http://localhost:8080`.
- Serve frontend via `python -m http.server` instead of opening HTML directly via `file://`.

## 10. Notes for End Users

- This project is configured for local development/demo.
- Razorpay integration is test-mode simulation.
- Use test cards/UPI in Razorpay checkout only.
