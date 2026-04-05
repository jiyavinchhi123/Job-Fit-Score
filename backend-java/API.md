# API Documentation

Base URL: `http://localhost:8080`

Headers:
- `X-Session-Id` (required on session-protected endpoints)
- `X-User-Id` (optional, required for authenticated user actions)

## Auth

### POST `/register`
Body:
```json
{ "email": "student@example.com", "password": "secret123" }
```
Response:
```json
{ "userId": 1, "email": "student@example.com", "plan": "free", "message": "Registered successfully" }
```

### POST `/login`
Body same as register.

### GET `/me`
Returns current user metadata if `X-User-Id` is valid.

## Resume & Scan

### POST `/upload-resume`
Content-Type: `multipart/form-data`
- field: `file` (PDF/DOCX)

Response:
```json
{ "resumeId": 10, "message": "Resume uploaded successfully" }
```

### POST `/analyze-job`
Body:
```json
{ "resumeId": 10, "jobDescription": "Need Java, SQL, Docker..." }
```
Response:
```json
{
  "resultId": 99,
  "fitScore": 72,
  "matchedSkills": ["Java", "SQL"],
  "missingSkills": ["Docker"],
  "suggestions": "Add projects or experience using: Docker."
}
```

If free limit exceeded, returns HTTP `402`.

### GET `/scan-result/{id}`
Returns stored scan result for same session/user owner.

### GET `/dashboard/history`
Returns previous scans for user or anonymous session.

## Payments

### POST `/create-payment`
Body:
```json
{ "userId": 1, "amount": 19900 }
```
Response includes Razorpay checkout options:
```json
{
  "key": "rzp_test_xxx",
  "amount": 19900,
  "currency": "INR",
  "name": "Resume -> Job Fit Score",
  "description": "Pro Plan Upgrade",
  "orderId": "order_xxx",
  "userId": 1
}
```

### POST `/payment-webhook`
Body:
```json
{ "userId": 1, "razorpayPaymentId": "pay_xxx", "status": "success" }
```
- `success` upgrades user to `pro`
- `failure` stores failed payment event

