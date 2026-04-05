#!/usr/bin/env pwsh
Write-Host "Supabase run helper — will prompt for DB password and start backend"

# securePwd will be a SecureString
$securePwd = Read-Host -Prompt "Enter Supabase DB password" -AsSecureString
$bstr = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePwd)
$plainPwd = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($bstr)
[System.Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)

$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://aws-1-ap-south-1.pooler.supabase.com:5432/postgres?sslmode=require"
$env:SPRING_DATASOURCE_USERNAME = "postgres.hgeadgfewzewgmhhryqt"
$env:SPRING_DATASOURCE_PASSWORD = $plainPwd

Write-Host "Starting Spring Boot (backend-java) with Supabase env vars set..."
mvn -f backend-java spring-boot:run
