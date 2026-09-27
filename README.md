# Secure College Student Service Request System

**NMAM Institute of Technology – MCA First Year, Section A**  
Academic Project

---

## Overview

A backend REST API for submitting and managing student service requests (Wi-Fi, laboratory, classroom, library, hostel, maintenance issues). Built with Spring Boot and secured with JWT authentication, role-based access control, BCrypt password hashing, and input validation.

## Tech Stack

| Technology             | Purpose                          |
|------------------------|----------------------------------|
| Java 17                | Programming language             |
| Spring Boot 3.2        | Backend framework                |
| Spring Security        | Authentication & authorization   |
| Spring Data JPA        | Database access                  |
| H2                     | In-memory database               |
| JWT (jjwt)             | Authentication tokens            |
| BCrypt                 | Password hashing                 |
| Jakarta Validation     | Input validation                 |
| Swagger / OpenAPI      | API documentation                |
| JUnit 5 + MockMvc      | Testing                          |
| Bucket4j + Caffeine    | Rate limiting                    |
| GitHub Actions         | CI/CD pipeline                   |

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+

### Run
```bash
mvn spring-boot:run
```

The application starts on `http://localhost:8080`.

### Default Admin Account
| Email                | Password      |
|----------------------|---------------|
| admin@nmamit.ac.in   | Admin@12345   |

### API Documentation
Open [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) after starting the app.

### H2 Console
Open [http://localhost:8080/h2-console](http://localhost:8080/h2-console)  
JDBC URL: `jdbc:h2:mem:servicerequestdb`

## API Endpoints

| Method | Endpoint                          | Access  | Description            |
|--------|-----------------------------------|---------|------------------------|
| POST   | `/api/auth/register`              | Public  | Register student       |
| POST   | `/api/auth/login`                 | Public  | Login → JWT            |
| GET    | `/api/students/me`                | STUDENT | View own profile       |
| PUT    | `/api/students/me`                | STUDENT | Update own profile     |
| POST   | `/api/requests`                   | STUDENT | Create service request |
| GET    | `/api/requests`                   | STUDENT | View own requests      |
| GET    | `/api/admin/students`             | ADMIN   | View all students      |
| GET    | `/api/admin/requests`             | ADMIN   | View all requests      |
| PUT    | `/api/admin/requests/{id}/status` | ADMIN   | Update request status  |

## Sample Requests (cURL)

### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Rahul Kumar",
    "email": "rahul@example.com",
    "password": "Rahul@12345",
    "phone": "9876543210",
    "course": "MCA",
    "year": 1
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "rahul@example.com", "password": "Rahul@12345"}'
```

### Create Service Request
```bash
curl -X POST http://localhost:8080/api/requests \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "title": "Wi-Fi not working",
    "description": "Wi-Fi is not working in Lab 2.",
    "category": "WIFI"
  }'
```

## Testing

```bash
# Run all tests
mvn test

# OWASP Dependency-Check
mvn org.owasp:dependency-check-maven:check

# Semgrep SAST
semgrep --config p/java --config p/owasp-top-ten src/main/java
```

## Security Features

- ✅ JWT authentication with signed tokens
- ✅ BCrypt password hashing
- ✅ Role-based access control (STUDENT / ADMIN)
- ✅ BOLA/IDOR prevention via ownership checks
- ✅ Input validation on all user-controlled fields
- ✅ SQL injection protection (JPA parameterized queries)
- ✅ Safe error responses (no stack traces)
- ✅ Rate limiting (IP-based, configurable)
- ✅ No hard-coded secrets (env var overrides)
- ✅ DTOs prevent entity exposure

## Project Structure

```
src/
├── main/java/com/nmamit/service/
│   ├── config/            # OpenAPI, DataInitializer
│   ├── controller/        # REST endpoints
│   ├── dto/               # Request/Response DTOs
│   ├── entity/            # JPA entities & enums
│   ├── exception/         # Global error handling
│   ├── repository/        # Spring Data repositories
│   ├── security/          # JWT, filters, SecurityConfig
│   └── service/           # Business logic
├── main/resources/
│   └── application.properties
└── test/
    ├── java/              # Integration tests
    └── resources/          # Test config
.github/workflows/ci.yml   # DevSecOps CI/CD pipeline
```
