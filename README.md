# 🚀 Workflow Approval Management System

यो project simple तर production-ready workflow approval management system हो — built with love by a Nepali developer! 💪

---

## 📋 Project Overview

यो systemले तपाईंको organization को approval workflow manage गर्छ। Employee ले request create गर्छ, अनि Manager/Admin ले approve, reject, वा changes request गर्न सक्छन्। Full audit trail साथ!

**Key Features:**
- Role-based access (ADMIN, MANAGER, EMPLOYEE)
- Multi-level approval workflow
- Approve / Reject / Request Changes
- Full history/audit trail
- JWT-based authentication
- Production-ready Docker setup
- Railway deployment ready

---

## 🛠️ Tech Stack

| Technology | Version |
|---|---|
| Java | 17 |
| Spring Boot | 3.2.3 |
| Spring Security + JWT | JJWT 0.11.5 |
| PostgreSQL | 15 |
| Maven | 3.x |
| Docker + Docker Compose | Latest |
| MapStruct | 1.5.5.Final |
| Lombok | 1.18.30 |

---

## 💻 Local Setup (PostgreSQL आफैले चलाउने)

### Prerequisites
- Java 17 installed हुनुपर्छ
- Maven installed हुनुपर्छ
- PostgreSQL installed and running हुनुपर्छ

### Step 1: Database Create गर्नुस्

PostgreSQL मा login गरेर:

```sql
CREATE DATABASE workflow_db;
```

Default config:
- **Host:** localhost:5432
- **Database:** workflow_db
- **Username:** postgres
- **Password:** 1234

### Step 2: Application Run गर्नुस्

```bash
# Project directory मा जानुस्
cd workflow-approval-system

# Build गर्नुस्
mvn clean install

# Run गर्नुस्
mvn spring-boot:run
```

Application start हुन्छ at: `http://localhost:8080`

Health check: `http://localhost:8080/actuator/health`

---

## 🐳 Docker सँग Run गर्ने (सबैभन्दा सजिलो तरिका!)

Docker installed छ भने एकदमै simple छ:

```bash
# Project directory मा जानुस्
cd workflow-approval-system

# Build and start everything
docker-compose up --build
```

बस्! यति मात्र गर्दा PostgreSQL र Application दुवै start हुन्छन्। 🎉

**Stop गर्न:**
```bash
docker-compose down
```

**Database data पनि delete गर्न:**
```bash
docker-compose down -v
```

---

## 🚂 Railway Deployment Steps

Railway मा deploy गर्न एकदम सजिलो छ, bhai!

### Step 1: Railway Account
1. [railway.app](https://railway.app) मा account बनाउनुस्
2. New Project create गर्नुस्

### Step 2: PostgreSQL Add गर्नुस्
1. Project मा "+ New" click गर्नुस्
2. "Database" → "PostgreSQL" select गर्नुस्
3. Railway ले automatically DATABASE_URL दिन्छ

### Step 3: Application Deploy गर्नुस्
1. GitHub repository connect गर्नुस्  
   **वा** Railway CLI use गर्नुस्:
   ```bash
   railway login
   railway up
   ```

### Step 4: Environment Variables Set गर्नुस्
Railway dashboard मा जाएर "Variables" tab मा यी set गर्नुस्:

```
DATABASE_URL=<Railway ले दिएको PostgreSQL URL>
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=<Railway PostgreSQL password>
JWT_SECRET=your-very-secure-secret-key-here-minimum-32-chars
JWT_EXPIRATION_MS=86400000
DDL_AUTO=update
```

> **Note:** Railway automatically `PORT` variable set गर्छ। हाम्रो application ले `${PORT:8080}` use गर्छ त्यसैले automatic works!

---

## 🔑 API Testing Guide

### Step 1: Users Register गर्नुस्

**Admin user create गर्नुस्:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin_user",
    "email": "admin@company.com",
    "password": "password123",
    "fullName": "Admin User",
    "role": "ADMIN"
  }'
```

**Manager user create गर्नुस्:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "manager_user",
    "email": "manager@company.com",
    "password": "password123",
    "fullName": "Manager User",
    "role": "MANAGER"
  }'
```

**Employee user create गर्नुस्:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "employee_user",
    "email": "employee@company.com",
    "password": "password123",
    "fullName": "Employee User",
    "role": "EMPLOYEE"
  }'
```

### Step 2: Login गर्नुस्

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "employee_user",
    "password": "password123"
  }'
```

Response मा `token` आउँछ — त्यो copy गर्नुस्।

### Step 3: Workflow Create गर्नुस्

```bash
curl -X POST http://localhost:8080/api/workflows \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Budget Approval Q1 2024",
    "description": "Marketing budget approval request",
    "approvalSteps": [
      {"approverId": 2, "level": 1, "stepName": "Manager Review"},
      {"approverId": 1, "level": 2, "stepName": "Admin Final Approval"}
    ]
  }'
```

### Step 4: Workflow Submit गर्नुस्

```bash
curl -X POST http://localhost:8080/api/workflows/1/submit \
  -H "Authorization: Bearer EMPLOYEE_TOKEN"
```

### Step 5: Approve/Reject गर्नुस् (Manager को token use गर्नुस्)

```bash
# Approve
curl -X POST http://localhost:8080/api/workflows/1/approve \
  -H "Authorization: Bearer MANAGER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"action": "APPROVE", "comments": "Looks good!"}'

# Reject
curl -X POST http://localhost:8080/api/workflows/1/approve \
  -H "Authorization: Bearer MANAGER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"action": "REJECT", "comments": "Budget too high"}'

# Request Changes
curl -X POST http://localhost:8080/api/workflows/1/approve \
  -H "Authorization: Bearer MANAGER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"action": "REQUEST_CHANGES", "comments": "Please add breakdown"}'
```

### Step 6: History Check गर्नुस्

```bash
curl -X GET http://localhost:8080/api/workflows/1/history \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 📬 Postman Collection

Project मा `WorkflowApprovalSystem.postman_collection.json` file छ।

1. Postman open गर्नुस्
2. Import → File → `WorkflowApprovalSystem.postman_collection.json` select गर्नुस्
3. Collection variable `baseUrl` check गर्नुस्
4. Register request run गर्दा automatically `token` set हुन्छ

---

## 🏗️ Project Structure

```
workflow-approval-system/
├── src/
│   ├── main/
│   │   ├── java/com/workflow/
│   │   │   ├── WorkflowApprovalApplication.java
│   │   │   ├── config/          # Security, JWT config
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/
│   │   │   │   ├── request/     # Request DTOs (Records)
│   │   │   │   └── response/    # Response DTOs (Records)
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── enums/           # Role, WorkflowStatus, ApprovalAction
│   │   │   ├── exception/       # Custom exceptions + Global handler
│   │   │   ├── mapper/          # MapStruct mappers
│   │   │   ├── repository/      # Spring Data JPA repos
│   │   │   ├── security/        # JWT filter, UserDetailsService
│   │   │   └── service/         # Service interfaces + implementations
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/workflow/
│           ├── WorkflowApprovalApplicationTest.java
│           ├── service/         # Unit tests
│           └── integration/     # Integration tests
├── Dockerfile
├── docker-compose.yml
├── .env.example
├── pom.xml
└── WorkflowApprovalSystem.postman_collection.json
```

---

## 🔒 API Endpoints Summary

### Auth (Public)
| Method | Endpoint | Description |
|---|---|---|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | Login & get token |

### Users (Authenticated)
| Method | Endpoint | Role Required |
|---|---|---|
| GET | /api/users/me | Any |
| GET | /api/users/{id} | Any |
| GET | /api/users | ADMIN |
| GET | /api/users/by-role/{role} | ADMIN, MANAGER |
| PATCH | /api/users/{id}/deactivate | ADMIN |

### Workflows (Authenticated)
| Method | Endpoint | Description |
|---|---|---|
| POST | /api/workflows | Create workflow |
| GET | /api/workflows/my | Get my workflows |
| GET | /api/workflows/{id} | Get by ID |
| GET | /api/workflows | All workflows (ADMIN/MANAGER) |
| GET | /api/workflows/status/{status} | Filter by status |
| POST | /api/workflows/{id}/submit | Submit for approval |
| POST | /api/workflows/{id}/approve | Process approval action |
| POST | /api/workflows/{id}/cancel | Cancel workflow |
| GET | /api/workflows/pending-for-me | Pending approvals for me |
| GET | /api/workflows/{id}/history | Audit trail |

---

## ✅ CI/CD Readiness

यो project CI/CD को लागि ready छ:

- **Environment variables** मार्फत configuration — hardcoded values छैनन्
- **Health check endpoint** available छ: `/actuator/health`
- **Docker multi-stage build** — lightweight production image
- **Test profile** छ H2 database सँग — CI मा PostgreSQL चाहिँदैन
- **Maven Wrapper** add गर्न सकिन्छ for consistent builds:
  ```bash
  mvn wrapper:wrapper
  ```

**GitHub Actions example:**
```yaml
- name: Build and Test
  run: mvn clean verify
  
- name: Docker Build
  run: docker build -t workflow-app .
```

---

## 🐛 Troubleshooting

**Application start हुँदैन?**
- PostgreSQL running छ? `pg_isready` check गर्नुस्
- `workflow_db` database exist गर्छ?
- Java 17 installed छ? `java -version` check गर्नुस्

**JWT invalid भन्छ?**
- Token expired भयो — फेरि login गर्नुस्
- `Authorization: Bearer <token>` header ठीक छ?

**Docker मा connection refused?**
- `docker-compose down -v && docker-compose up --build` try गर्नुस्

---

## 📝 License

MIT License — free to use, modify, and deploy!

---

*Made with ❤️ and धेरै chai ☕ — keep coding, keep growing! 🇳🇵*
