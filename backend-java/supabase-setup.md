Supabase setup and local run instructions
=======================================

1) Connection details

- Host: aws-1-ap-south-1.pooler.supabase.com
- Port: 5432
- Database: postgres
- User: postgres.hgeadgfewzewgmhhryqt
- Password: (your Supabase DB password — not stored here)

2) JDBC URL (use SSL)

jdbc:postgresql://aws-1-ap-south-1.pooler.supabase.com:5432/postgres?sslmode=require

3) Set environment variables (PowerShell)

Run these commands in PowerShell (replace <your-db-password>):

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://aws-1-ap-south-1.pooler.supabase.com:5432/postgres?sslmode=require"
$env:SPRING_DATASOURCE_USERNAME = "postgres.hgeadgfewzewgmhhryqt"
$env:SPRING_DATASOURCE_PASSWORD = "<your-db-password>"
mvn -f backend-java spring-boot:run
```

Or use the provided script which will prompt for the password securely:

```powershell
# Run from repository root
.\backend-java\run_supabase.ps1
```

4) Apply schema

- Open the Supabase project dashboard → SQL Editor and run `database/schema_postgres.sql`.
- Alternatively use `psql` with the same JDBC/connection details.

5) Notes

- `application.properties` in `backend-java` reads env vars:
  - `SPRING_DATASOURCE_URL`
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`
- `spring.jpa.hibernate.ddl-auto=update` is enabled; if you prefer explicit control, run the SQL file instead.
