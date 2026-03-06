# Resume -> Job Fit Score

Micro-SaaS hackathon project with architecture:

`Frontend (HTML/CSS/JS) -> Spring Boot API -> Flask AI Service -> MySQL (XAMPP)`

## 1. Folder Structure

```text
/frontend
  index.html
  result.html
  pricing.html
  auth.html
  dashboard.html
  style.css
  upload.js
  result.js
  /js
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

## 2. Prerequisites

- Java 17+
- Maven 3.9+
- Python 3.10+
- XAMPP MySQL running on port 3306

## 3. Setup & Run

1. Create schema in MySQL (phpMyAdmin or mysql CLI):
```sql
SOURCE c:/Users/jiyav/Desktop/Hackamind/database/schema.sql;
```

2. Start AI service:
```powershell
cd c:\Users\jiyav\Desktop\Hackamind\ai-service
python -m venv .venv
.\.venv\Scripts\activate
pip install -r requirements.txt
python app.py
```

3. Start Java backend:
```powershell
cd c:\Users\jiyav\Desktop\Hackamind\backend-java
mvn spring-boot:run
```

4. Start frontend (any static server):
```powershell
cd c:\Users\jiyav\Desktop\Hackamind\frontend
python -m http.server 5500
```
Open `http://localhost:5500`.

## 4. End-to-End User Flow

1. Open home page.
2. Upload PDF/DOCX resume.
3. Paste job description.
4. Click `Check Fit Score`.
5. First 3 scans work without login (anonymous via `localStorage` session ID).
6. On 4th scan, paywall appears.
7. Register/Login (merges anonymous session data to user).
8. Upgrade via Razorpay test checkout on Pricing page.
9. After payment webhook success, user plan becomes `pro`.

## 5. Security & Validation Implemented

- Resume file type validation: PDF/DOCX only.
- File size limit: 5MB.
- Password hashing with BCrypt.
- SQL injection resistance via Spring Data JPA parameterized statements.

## 6. API Docs

See:
- `backend-java/API.md`

