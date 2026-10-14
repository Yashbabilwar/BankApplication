# BANK-101 changes applied

## Reused existing classes
- `entity/User.java`
- `repository/UserRepository.java`
- `dto/RegisterRequest.java`
- `dto/LoginRequest.java`
- `dto/RegisterResponse.java`
- `dto/LoginResponse.java`
- `service/AuthService.java`
- `controller/UserAuth.java`
- Existing custom exceptions

## Updated
- `security/JwtUtil.java`: reads Base64 JWT secret from environment configuration; creates signed expiring tokens and parses/verifies them.
- `security/SecurityConfig.java`: stateless setup, disables form/basic login, configures JSON 401 and registers JWT filter.
- `service/AuthServiceImpl.java`: normalizes email, checks duplicate email/mobile, hashes passwords with BCrypt, validates login and returns JWT.
- `exception/GlobalExceptionHandler.java`: validation, malformed JSON, duplicate conflicts, invalid credentials, and generic error responses.
- Request DTO validation constraints.
- `application.properties`: configurable database settings and required JWT secret.

## Added
- `security/JwtAuthenticationFilter.java`
- `controller/CustomerProfileController.java` (protected endpoint for verifying JWT)
- `AuthServiceImplTest.java`
- H2 test dependency and isolated context-load test properties.
- `BANK-101-README.md` with setup and Postman instructions.

## Verification note
The POM XML and Java brace scan passed. Maven tests could not be executed in this environment because Maven Wrapper could not download Maven from Maven Central (network fetch failed). Run `mvnw.cmd test` on your Windows machine after Maven dependencies are available.
