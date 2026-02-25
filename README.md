# Workflow Approval Management System

A simple but production-ready workflow approval management system.

---

## Project Overview

This system manages your organization's approval workflow.

* Employees create requests.
* Managers/Admins can approve, reject, or request changes.
* Full audit trail is maintained.

Key Features:

* Role-based access control (ADMIN, MANAGER, EMPLOYEE)
* Multi-level approval workflow
* Approve / Reject / Request Changes
* Full history and audit trail
* JWT-based authentication
* Production-ready Docker setup
* Railway deployment ready

---

## Tech Stack

Technology              Version

---

Java                    17
Spring Boot             3.2.3
Spring Security + JWT   JJWT 0.11.5
PostgreSQL              15
Maven                   3.x
Docker                  Latest
MapStruct               1.5.5.Final
Lombok                  1.18.30

---

## Local Setup (Manual PostgreSQL Setup)

Prerequisites:

* Java 17 installed
* Maven installed
* PostgreSQL installed and running

Step 1: Create Database

Login to PostgreSQL and run:

```
CREATE DATABASE workflow_db;
```

Default configuration:

Host:       localhost:5432
Database:   workflow_db
Username:   postgres
Password:   1234

Step 2: Run Application

```
cd workflow-approval-system
mvn clean install
mvn spring-boot:run
```

Application URL:
[http://localhost:8080](http://localhost:8080)

Health Check:
[http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

---

## Docker Setup (Recommended)

Run everything with Docker:

```
cd workflow-approval-system
docker-compose up --build
```

Stop:

```
docker-compose down
```

Remove database volume:

```
docker-compose down -v
```

---

## Railway Deployment Steps

Step 1: Create Railway Account

* Go to [https://railway.app](https://railway.app)
* Create new project

Step 2: Add PostgreSQL

* Click "+ New"
* Select Database -> PostgreSQL
* Railway generates DATABASE_URL automatically

Step 3: Deploy Application

Option 1: Connect GitHub repository

Option 2: Use Railway CLI:

```
railway login
railway up
```

Step 4: Set Environment Variables

Set the following variables in Railway dashboard:

```
DATABASE_URL=<Railway PostgreSQL URL>
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=<Railway PostgreSQL password>
JWT_SECRET=your-very-secure-secret-key-minimum-32-chars
JWT_EXPIRATION_MS=86400000
DDL_AUTO=update
```

Note:
Railway automatically sets PORT.
Application uses ${PORT:8080} so it works automatically.

---

## API Testing Guide

Step 1: Register Users

Create Admin:

```
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

Create Manager:

```
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

Create Employee:

```
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

Step 2: Login

```
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "employee_user",
    "password": "password123"
  }'
```

Copy the token from the response.

Step 3: Create Workflow

```
curl -X POST http://localhost:8080/api/workflows \
  -H "Authorization: Bearer YOUR_TOKEN" \
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

Step 4: Submit Workflow

```
curl -X POST http://localhost:8080/api/workflows/1/submit \
  -H "Authorization: Bearer EMPLOYEE_TOKEN"
```

Step 5: Approve / Reject

Approve:

```
curl -X POST http://localhost:8080/api/workflows/1/approve \
  -H "Authorization: Bearer MANAGER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"action": "APPROVE", "comments": "Looks good!"}'
```

Reject:

```
curl -X POST http://localhost:8080/api/workflows/1/approve \
  -H "Authorization: Bearer MANAGER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"action": "REJECT", "comments": "Budget too high"}'
```

Request Changes:

```
curl -X POST http://localhost:8080/api/workflows/1/approve \
  -H "Authorization: Bearer MANAGER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"action": "REQUEST_CHANGES", "comments": "Please add breakdown"}'
```

Step 6: Check History

```
curl -X GET http://localhost:8080/api/workflows/1/history \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Project Structure

```
workflow-approval-system/
├── src/
│   ├── main/java/com/workflow/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── enums/
│   │   ├── exception/
│   │   ├── mapper/
│   │   ├── repository/
│   │   ├── security/
│   │   └── service/
│   └── resources/application.yml
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── WorkflowApprovalSystem.postman_collection.json
```

---

## License

MIT License


