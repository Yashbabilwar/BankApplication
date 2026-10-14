# BANK-101 Authentication Module

## Existing package structure

`com.bank.bankapplication`
- `entity/User.java` - customer entity
- `repository/UserRepository.java` - JPA repository
- `dto/` - request and response DTOs
- `service/AuthService.java`, `service/AuthServiceImpl.java` - auth business logic
- `controller/UserAuth.java` - registration/login endpoints
- `security/JwtUtil.java`, `security/JwtAuthenticationFilter.java`, `security/SecurityConfig.java` - JWT and security
- `exception/` - API error handling
- `controller/CustomerProfileController.java` - protected JWT test endpoint

## Local setup

1. Create the database if it does not exist: `CREATE DATABASE erp_db;`
2. Configure `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` environment variables if your local MySQL settings differ from the defaults.
3. Generate a Base64 JWT secret using PowerShell:

```powershell
$bytes = New-Object byte[] 32
[System.Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
[Convert]::ToBase64String($bytes)
```

4. Set the resulting value as environment variable `JWT_SECRET` in your IDE run configuration. Do not commit the secret.
5. Reload Maven and run `BankapplicationApplication`.

## API tests (port 9706)

### Register
`POST http://localhost:9706/api/auth/register`
`Content-Type: application/json`

```json
{
  "firstName": "Vaishnavi",
  "lastName": "More",
  "email": "vaishnavi@example.com",
  "mobileNumber": "9876543210",
  "password": "Password@123"
}
```

Expected: `201 Created`; password must not be in response.

### Login
`POST http://localhost:9706/api/auth/login`

```json
{
  "email": "vaishnavi@example.com",
  "password": "Password@123"
}
```

Expected: `200 OK`, with JWT in `token`.

### Protected endpoint
`GET http://localhost:9706/api/customer/profile`

Use Postman Authorization > Bearer Token and paste the login token. Expected: `200 OK`. Without a token or with an invalid/expired token, expected: `401 Unauthorized`.

## Acceptance checks
- Duplicate email/mobile: `409 Conflict`
- Invalid request fields: `400 Bad Request`
- Wrong email/password: `401 Unauthorized`
- Password saved with BCrypt, never returned by APIs
- JWT signature and expiry checked by the request filter
