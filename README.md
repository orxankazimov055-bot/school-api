
# School API

Spring Boot ilə hazırlanmış məktəb idarəetmə sistemi.

## Texnologiyalar

- Java 25
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (JJWT)
- Gradle
- Docker
- Swagger / OpenAPI
- Lombok

## Təhlükəsizlik

- JWT Access və Refresh Token
- BCrypt ilə şifrələrin hash edilməsi
- ADMIN, MANAGER və USER rolları
- Token Blacklist
- Refresh Token Rotation
- Refresh Token Reuse Detection
- Sessiyaların idarə olunması
- Logout və Logout All
- Şifrənin dəyişdirilməsi
- Hesabların aktiv və deaktiv edilməsi
- Token Cleanup Scheduler
- Azərbaycan, ingilis və rus dillərində xəta mesajları

## Environment Variables

Layihəni işə salmazdan əvvəl aşağıdakı mühit dəyişənləri təyin edilməlidir:

| Variable | Description |
|---|---|
| DB_PASSWORD | PostgreSQL şifrəsi |
| JWT_SECRET | Base64 formatında JWT secret |
| ADMIN_PASSWORD | Admin hesabının şifrəsi |

IntelliJ IDEA:

Run → Edit Configurations → Environment variables

Real şifrələr və JWT secret GitHub reposunda saxlanılmamalıdır.

## Layihənin işə salınması

```powershell
docker compose up -d
.\gradlew bootRun
```

## Swagger

http://localhost:8080/test-demo/swagger-ui/index.html

## Authentication Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | /api/auth/register | Yeni hesab |
| POST | /api/auth/login | Sistemə giriş |
| POST | /api/auth/refresh | Token yeniləmə |
| POST | /api/auth/logout | Cari sessiyadan çıxış |
| POST | /api/auth/logout-all | Bütün sessiyalardan çıxış |
| GET | /api/auth/me | Hesab məlumatları |
| GET | /api/auth/sessions | Aktiv sessiyalar |
| DELETE | /api/auth/sessions/{sessionId} | Sessiyanın ləğvi |
| PUT | /api/auth/password | Şifrəni dəyişmək |

## Token müddətləri

- Access Token: 15 dəqiqə
- Refresh Token: 7 gün

## Security Errors

- 401 Unauthorized
- 403 Forbidden
- ErrorCode
- TraceId
- Internationalization (i18n)
