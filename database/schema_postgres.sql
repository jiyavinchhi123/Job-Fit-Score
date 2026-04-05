-- PostgreSQL-compatible schema (for Supabase)
-- Supabase already provides a database; run these statements in SQL Editor

-- Users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    plan VARCHAR(20) NOT NULL DEFAULT 'free',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sessions
CREATE TABLE IF NOT EXISTS sessions (
    session_id VARCHAR(100) PRIMARY KEY,
    user_id BIGINT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sessions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Resumes
CREATE TABLE IF NOT EXISTS resumes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NULL,
    session_id VARCHAR(100) NULL,
    resume_text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_resumes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_resumes_session FOREIGN KEY (session_id) REFERENCES sessions(session_id) ON DELETE SET NULL
);

-- Job descriptions
CREATE TABLE IF NOT EXISTS job_descriptions (
    id BIGSERIAL PRIMARY KEY,
    resume_id BIGINT NOT NULL,
    job_text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_jd_resume FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- Scan results
CREATE TABLE IF NOT EXISTS scan_results (
    id BIGSERIAL PRIMARY KEY,
    resume_id BIGINT NOT NULL,
    fit_score INT NOT NULL,
    matched_skills TEXT NOT NULL,
    missing_skills TEXT NOT NULL,
    suggestions TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_scan_resume FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- Payments
CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    razorpay_payment_id VARCHAR(120) NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payments_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
