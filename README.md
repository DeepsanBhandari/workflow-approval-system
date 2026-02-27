<h1 align="center"> Workflow Approval Management System</h1>

<p align="center">
Production-ready backend system for managing multi-level approval workflows with secure role-based access control, audit trails, and cloud deployment.
</p>

<hr/>

<h2>🌍 Live Production Deployment</h2>

<p>
<strong>Base URL:</strong><br/>
<a href="https://workflow-approval-system-production.up.railway.app" target="_blank">
https://workflow-approval-system-production.up.railway.app
</a>
</p>

<p>
<strong>Health Check:</strong><br/>
<a href="https://workflow-approval-system-production.up.railway.app/actuator/health" target="_blank">
/actuator/health
</a>
</p>

<hr/>

<h2>Overview</h2>

<p>
This system enables organizations to manage structured approval workflows instead of relying on emails or spreadsheets.
</p>

<ul>
<li>Employees submit requests</li>
<li>Managers review at Level 1</li>
<li>Admins provide final approval</li>
<li>Full audit tracking of every action</li>
<li>Strict workflow state transition validation</li>
</ul>

<hr/>

<h2>🏗 Architecture</h2>

<ul>
<li>Layered Architecture (Controller → Service → Repository)</li>
<li>DTO Pattern using MapStruct</li>
<li>Stateless JWT Authentication</li>
<li>PostgreSQL relational database</li>
<li>Dockerized container setup</li>
<li>Cloud deployment via Railway</li>
</ul>

<hr/>

<h2> Core Features</h2>

<ul>
<li>Role-Based Access Control (ADMIN, MANAGER, EMPLOYEE)</li>
<li>Multi-level sequential approval workflow</li>
<li>Approve / Reject / Request Changes</li>
<li>Full workflow history & audit trail</li>
<li>JWT authentication & authorization</li>
<li>Global exception handling</li>
<li>Environment-based configuration</li>
<li>Docker & docker-compose support</li>
</ul>

<hr/>

<h2>🛠 Tech Stack</h2>

<table>
<tr>
<th>Technology</th>
<th>Version</th>
</tr>
<tr>
<td>Java</td>
<td>17</td>
</tr>
<tr>
<td>Spring Boot</td>
<td>3.2.3</td>
</tr>
<tr>
<td>Spring Security</td>
<td>Latest</td>
</tr>
<tr>
<td>JWT (JJWT)</td>
<td>0.11.5</td>
</tr>
<tr>
<td>PostgreSQL</td>
<td>15</td>
</tr>
<tr>
<td>Maven</td>
<td>3.x</td>
</tr>
<tr>
<td>Docker</td>
<td>Latest</td>
</tr>
<tr>
<td>MapStruct</td>
<td>1.5.5.Final</td>
</tr>
<tr>
<td>Lombok</td>
<td>1.18.30</td>
</tr>
</table>

<hr/>

<h2>🖥 Running Locally (Manual Setup)</h2>

<h3>Prerequisites</h3>
<ul>
<li>Java 17</li>
<li>Maven</li>
<li>PostgreSQL running locally</li>
</ul>

<h3>Step 1 — Create Database</h3>

<pre>
CREATE DATABASE workflow_db;
</pre>

Default configuration:

<pre>
Host: localhost
Port: 5432
Database: workflow_db
Username: postgres
Password: 1234
</pre>

<h3>Step 2 — Build & Run</h3>

<pre>
mvn clean install
mvn spring-boot:run
</pre>

Application URL:

<pre>
http://localhost:8080
</pre>

Health Check:

<pre>
http://localhost:8080/actuator/health
</pre>

<hr/>

<h2>🐳 Run with Docker (Recommended)</h2>

<pre>
docker-compose up --build
</pre>

Stop containers:

<pre>
docker-compose down
</pre>

Remove database volume:

<pre>
docker-compose down -v
</pre>

<hr/>

<h2>☁ Cloud Deployment (Railway)</h2>

<p>
Deployed using Railway with managed PostgreSQL and environment-based configuration.
</p>

Required environment variables:

<pre>
DATABASE_URL=
DATABASE_USERNAME=
DATABASE_PASSWORD=
JWT_SECRET=
JWT_EXPIRATION_MS=86400000
DDL_AUTO=update
</pre>

<hr/>

<h2>🔄 API Usage Flow</h2>

<p><strong>Postman collection included:</strong></p>

<pre>
WorkflowApprovalSystem.postman_collection.json
</pre>

<h3>1️⃣ Register User</h3>
<pre>POST /api/auth/register</pre>

<h3>2️⃣ Login</h3>
<pre>POST /api/auth/login</pre>

Use JWT token in header:

<pre>
Authorization: Bearer &lt;TOKEN&gt;
</pre>

<h3>3️⃣ Create Workflow</h3>
<pre>POST /api/workflows</pre>

<h3>4️⃣ Submit Workflow</h3>
<pre>POST /api/workflows/{id}/submit</pre>

<h3>5️⃣ Approve / Reject / Request Changes</h3>
<pre>POST /api/workflows/{id}/approve</pre>

Supported actions:
<ul>
<li>APPROVE</li>
<li>REJECT</li>
<li>REQUEST_CHANGES</li>
</ul>

<h3>6️⃣ View Audit History</h3>
<pre>GET /api/workflows/{id}/history</pre>

<hr/>

<h2>📂 Project Structure</h2>

<pre>
src/main/java/com/workflow/
 ├── config/
 ├── controller/
 ├── dto/
 ├── entity/
 ├── enums/
 ├── exception/
 ├── mapper/
 ├── repository/
 ├── security/
 └── service/
</pre>

<hr/>

<h2>🔒 Security Implementation</h2>

<ul>
<li>BCrypt password hashing</li>
<li>JWT token validation filter</li>
<li>Role-based endpoint protection</li>
<li>Stateless authentication</li>
</ul>

<hr/>

<h2>📈 Backend Skills Demonstrated</h2>

<ul>
<li>Secure REST API design</li>
<li>Business rule enforcement</li>
<li>Database relationship modeling</li>
<li>Multi-level workflow logic</li>
<li>Production containerization</li>
<li>Cloud deployment configuration</li>
</ul>

<hr/>

<h2>📜 License</h2>

<p>MIT License</p>
