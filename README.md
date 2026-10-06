# DentalBot 🦷

DentalBot is a **multi-tenant missed-call recovery MVP for dental clinics**.

It converts missed calls into structured leads, collects appointment details through WhatsApp-style conversations, and allows clinic staff to confirm or dismiss appointments from a dashboard.

---

## ✨ Features

- 🔐 JWT-based authentication
- 🏥 Multi-tenant clinic architecture
- 👤 Clinic owner and receptionist roles
- 📞 Missed-call lead creation
- 💬 WhatsApp conversation flow
- 🤖 Automated conversational lead collection
- 📋 Lead management
- ✅ Appointment confirmation
- ❌ Lead dismissal
- 📅 Appointment management
- 📊 Dashboard summary
- 🏢 Clinic profile management
- 🗄️ PostgreSQL database
- 🛠️ Flyway database migrations
- 🐳 Dockerized PostgreSQL
- 🔒 Clinic-level data isolation
- ⚠️ Centralized exception handling
- 📱 Twilio WhatsApp integration

---

## 🏗️ Architecture

```text
Patient
   │
   │ Missed Call
   ▼
DentalBot Backend
   │
   ├── Authentication
   │      └── JWT
   │
   ├── Lead Management
   │      ├── Create Lead
   │      ├── Collect Details
   │      ├── Confirm Lead
   │      └── Dismiss Lead
   │
   ├── Conversation Management
   │      ├── Conversation State
   │      └── Message History
   │
   ├── WhatsApp Integration
   │      └── Twilio
   │
   ├── Appointment Management
   │
   ├── Dashboard
   │
   └── Clinic Management
          │
          ▼
      PostgreSQL
```

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot |
| Security | Spring Security + JWT |
| Authentication | BCrypt |
| Database | PostgreSQL |
| Database Migration | Flyway |
| ORM | Spring Data JPA / Hibernate |
| API | REST |
| Messaging | Twilio WhatsApp |
| Build Tool | Maven |
| Containerization | Docker |
| API Testing | Postman |
| Development IDE | IntelliJ IDEA |

---

## 📁 Project Structure

```text
dentalbot/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── riya/
│   │   │           └── dentalbot/
│   │   │               ├── appointment/
│   │   │               ├── auth/
│   │   │               ├── clinic/
│   │   │               ├── conversation/
│   │   │               ├── dashboard/
│   │   │               ├── exception/
│   │   │               ├── lead/
│   │   │               ├── missedcall/
│   │   │               ├── security/
│   │   │               ├── user/
│   │   │               ├── whatsapp/
│   │   │               └── DentalbotApplication.java
│   │   │
│   │   └── resources/
│   │       ├── db/
│   │       │   └── migration/
│   │       └── application.yml
│   │
│   └── test/
│
├── docker-compose.yml
├── pom.xml
├── .env
└── README.md
```

---

# 🔐 Authentication

DentalBot uses **JWT-based stateless authentication**.

### Authentication Flow

```text
Register
   │
   ▼
Create Clinic
   │
   ▼
Create Owner
   │
   ▼
Hash Password with BCrypt
   │
   ▼
Login
   │
   ▼
Generate JWT
   │
   ▼
Send JWT in Authorization Header
   │
   ▼
JWT Filter
   │
   ▼
Authenticate Request
```

Protected requests use:

```http
Authorization: Bearer <JWT_TOKEN>
```

---

# 👥 User Roles

DentalBot currently supports two roles:

### OWNER

The clinic owner can:

- Register the clinic
- Login
- View profile
- Update clinic information
- View leads
- Confirm appointments
- Dismiss leads
- View dashboard information

### RECEPTIONIST

The receptionist can:

- View missed-call leads
- Read patient conversations
- Confirm appointments
- Dismiss leads
- View appointment information

---

# 📊 Lead Lifecycle

A lead moves through the following states:

```text
AWAITING_REPLY
       │
       ▼
COLLECTING_DETAILS
       │
       ▼
AWAITING_CONFIRMATION
       │
       ├───────────────┐
       ▼               ▼
  CONFIRMED        DISMISSED
       │
       ▼
  APPOINTMENT
```

### Lead Statuses

| Status | Description |
|---|---|
| `AWAITING_REPLY` | Waiting for the patient to respond |
| `COLLECTING_DETAILS` | Collecting appointment information |
| `AWAITING_CONFIRMATION` | Patient details collected and waiting for clinic confirmation |
| `CONFIRMED` | Lead has been confirmed |
| `DISMISSED` | Lead has been dismissed |

---

# 💬 Conversation Flow

The conversation state machine currently supports:

```text
ASKING_SERVICE
      │
      ▼
ASKING_NAME
      │
      ▼
ASKING_PREFERRED_TIME
      │
      ▼
COMPLETED
```

The conversation stores:

- Patient phone number
- Lead reference
- Current conversation state
- Opt-out status
- Conversation timestamps
- Message history

Messages contain:

- Sender type
- Message content
- Sent timestamp

---

# 📱 WhatsApp Integration

DentalBot currently uses **Twilio WhatsApp** for messaging.

The active WhatsApp implementation uses:

```text
Twilio SDK
   │
   ▼
WhatsAppService
   │
   ▼
Twilio WhatsApp API
   │
   ▼
Patient
```

The application also contains classes related to Meta WhatsApp Cloud API integration, but the current active messaging implementation is based on Twilio.

---

# 📞 Missed Call Flow

The missed-call endpoint starts the lead recovery process.

```text
Missed Call
    │
    ▼
POST /api/v1/missed-calls
    │
    ▼
Identify Authenticated Clinic
    │
    ▼
Create / Find Active Lead
    │
    ▼
Start Conversation
    │
    ▼
Send WhatsApp Message
    │
    ▼
Patient Responds
    │
    ▼
Conversation Continues
```

### Endpoint

```http
POST /api/v1/missed-calls
```

Example request:

```json
{
  "patientPhone": "9876543210"
}
```

The endpoint returns information indicating whether the lead was created and whether the WhatsApp message was successfully sent.

---

# 📲 WhatsApp Webhook

The Twilio webhook receives incoming WhatsApp messages.

### Endpoint

```http
POST /api/v1/whatsapp/webhook
```

Content type:

```text
application/x-www-form-urlencoded
```

The webhook receives:

```text
From
Body
```

The incoming message is passed to the conversation service and the generated response is returned using TwiML XML.

---

# 🏥 Clinic Management

Clinic information can be viewed and updated through the clinic API.

### Get Clinic

```http
GET /api/v1/clinic
```

### Update Clinic

```http
PUT /api/v1/clinic
```

Example:

```json
{
  "name": "Smile Dental Clinic",
  "phone": "9876543210",
  "address": "Main Road",
  "whatsappPhoneNumberId": "your_whatsapp_phone_number_id"
}
```

---

# 👤 Lead Management

## Get All Leads

```http
GET /api/v1/leads
```

Returns leads belonging to the authenticated clinic.

---

## Get Lead by ID

```http
GET /api/v1/leads/{id}
```

---

## Create Demo Lead

```http
POST /api/v1/leads/demo
```

This endpoint is useful for testing and demonstrations.

---

## Confirm Lead

```http
POST /api/v1/leads/{id}/confirm
```

Example:

```json
{
  "patientName": "Rahul Sharma",
  "service": "Dental Cleaning",
  "scheduledAt": "2026-10-10T11:00:00"
}
```

When a lead is confirmed:

```text
Lead
  │
  ▼
CONFIRMED
  │
  ▼
Appointment Created
```

---

## Dismiss Lead

```http
POST /api/v1/leads/{id}/dismiss
```

The lead status becomes:

```text
DISMISSED
```

---

# 📅 Appointment Management

## Get Appointments

```http
GET /api/v1/appointments
```

Appointments are returned for the authenticated clinic.

Each appointment contains:

```text
id
patientName
patientPhone
service
scheduledAt
status
createdAt
```

### Appointment Statuses

| Status | Description |
|---|---|
| `CONFIRMED` | Appointment confirmed |
| `COMPLETED` | Appointment completed |
| `CANCELLED` | Appointment cancelled |

---

# 📊 Dashboard

### Endpoint

```http
GET /api/v1/dashboard
```

The dashboard provides clinic-level statistics.

### Response Information

```text
totalLeads
awaitingReplyCount
collectingDetailsCount
awaitingConfirmationCount
confirmedCount
dismissedCount
todaysAppointments
recentLeads
```

Example response:

```json
{
  "totalLeads": 25,
  "awaitingReplyCount": 5,
  "collectingDetailsCount": 4,
  "awaitingConfirmationCount": 3,
  "confirmedCount": 10,
  "dismissedCount": 3,
  "todaysAppointments": [],
  "recentLeads": []
}
```

---

# 🔑 Authentication API

## Register

```http
POST /api/v1/auth/register
```

Example:

```json
{
  "clinicName": "Smile Dental Clinic",
  "ownerName": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

---

## Login

```http
POST /api/v1/auth/login
```

Example:

```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

The response contains a JWT token.

Use the token for protected endpoints:

```http
Authorization: Bearer <JWT_TOKEN>
```

---

## Get Current User

```http
GET /api/v1/auth/me
```

Returns information about the currently authenticated user.

---

# 🧪 Test Authentication

A simple protected test endpoint is available:

```http
GET /api/v1/test
```

Successful authentication returns:

```text
JWT Authentication Successful!
```

---

# 🗄️ Database

DentalBot uses:

```text
PostgreSQL 16
```

Main tables:

```text
clinic
users
lead
conversation
message
appointment
```

### Relationships

```text
Clinic
 ├── Users
 ├── Leads
 ├── Conversations
 └── Appointments

Lead
 ├── Conversation
 └── Appointment

Conversation
 └── Messages
```

---

# 🔒 Multi-Tenancy

DentalBot follows a clinic-based multi-tenant architecture.

Each authenticated user belongs to a clinic through:

```text
user.clinicId
```

Requests use the authenticated user's clinic ID to scope data.

For example:

```text
User A
  │
  └── Clinic A
        └── Leads A

User B
  │
  └── Clinic B
        └── Leads B
```

Clinic A cannot access Clinic B's leads.

This clinic-level isolation is applied to lead, appointment, dashboard, conversation, and clinic operations.

---

# 🐳 Docker Setup

PostgreSQL can be started using Docker Compose.

### Start PostgreSQL

```bash
docker compose up -d postgres
```

### Check running containers

```bash
docker ps
```

### Stop PostgreSQL

```bash
docker compose down
```

### Stop and remove the database volume

```bash
docker compose down -v
```

> Warning: removing the volume deletes the PostgreSQL data stored in the Docker volume.

---

# ⚙️ Environment Variables

Create a `.env` file locally.

**Do not commit your real `.env` file to GitHub.**

Example:

```env
POSTGRES_URL=jdbc:postgresql://localhost:5433/dentalbot
POSTGRES_DB=dentalbot
POSTGRES_USERNAME=postgres
POSTGRES_PASSWORD=your_postgres_password
POSTGRES_PORT=5433

JWT_SECRET=your_long_random_jwt_secret

TWILIO_ACCOUNT_SID=your_twilio_account_sid
TWILIO_AUTH_TOKEN=your_twilio_auth_token
TWILIO_WHATSAPP_FROM=whatsapp:+14155238886

TWILIO_APPOINTMENT_TEMPLATE_CONTENT_SID=your_twilio_template_content_sid
```

### Environment Variable Description

| Variable | Purpose |
|---|---|
| `POSTGRES_URL` | PostgreSQL JDBC URL |
| `POSTGRES_DB` | Database name |
| `POSTGRES_USERNAME` | PostgreSQL username |
| `POSTGRES_PASSWORD` | PostgreSQL password |
| `POSTGRES_PORT` | PostgreSQL port |
| `JWT_SECRET` | Secret used to sign JWTs |
| `TWILIO_ACCOUNT_SID` | Twilio account SID |
| `TWILIO_AUTH_TOKEN` | Twilio authentication token |
| `TWILIO_WHATSAPP_FROM` | Twilio WhatsApp sender |
| `TWILIO_APPOINTMENT_TEMPLATE_CONTENT_SID` | Twilio appointment template identifier |

---

# 🔐 Security

Sensitive credentials must never be committed to GitHub.

The following should remain private:

```text
JWT_SECRET
TWILIO_ACCOUNT_SID
TWILIO_AUTH_TOKEN
POSTGRES_PASSWORD
```

A safe repository should contain only placeholders.

Example:

```env
JWT_SECRET=your_jwt_secret
TWILIO_ACCOUNT_SID=your_account_sid
TWILIO_AUTH_TOKEN=your_auth_token
```

Add `.env` to `.gitignore`:

```gitignore
.env
```

---

# 🛠️ Database Migrations

Flyway is used for database schema management.

Migration files are located at:

```text
src/main/resources/db/migration/
```

Example:

```text
V1__initial_schema.sql
```

The application uses:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

This means Hibernate validates the existing database schema instead of automatically creating or modifying tables.

Flyway is responsible for applying database migrations.

---

# ▶️ Running the Project Locally

## 1. Clone the Repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd dentalbot
```

---

## 2. Start PostgreSQL

```bash
docker compose up -d postgres
```

---

## 3. Configure Environment Variables

Create:

```text
.env
```

and add the required variables.

---

## 4. Run the Spring Boot Application

Using Maven:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

Or run:

```text
DentalbotApplication
```

from IntelliJ IDEA.

---

## 5. Application URL

The backend runs on:

```text
http://localhost:8080
```

---

# 🧪 API Testing with Postman

A typical testing flow is:

```text
1. Register
      ↓
2. Login
      ↓
3. Copy JWT
      ↓
4. Add JWT to Authorization header
      ↓
5. Create / View Leads
      ↓
6. Confirm or Dismiss Lead
      ↓
7. View Appointments
      ↓
8. View Dashboard
```

For protected requests:

```http
Authorization: Bearer <JWT_TOKEN>
```

---

# 🎬 Demo Flow

A complete DentalBot demo can be performed using the following flow:

```text
Register Clinic
      ↓
Login
      ↓
Get JWT
      ↓
Trigger Missed Call
      ↓
Lead Created
      ↓
WhatsApp Message Sent
      ↓
Patient Responds
      ↓
Bot Collects:
   - Service
   - Patient Name
   - Preferred Time
      ↓
Lead → AWAITING_CONFIRMATION
      ↓
Clinic Staff Confirms Lead
      ↓
Lead → CONFIRMED
      ↓
Appointment Created
      ↓
Dashboard Updated
```

For development/demo purposes, the demo lead endpoint can also be used to quickly create a lead in the `AWAITING_CONFIRMATION` state.

---

# ⚠️ Twilio Sandbox Limitation

When using the Twilio WhatsApp Sandbox, WhatsApp messaging may require the recipient to first join/interact with the sandbox according to Twilio's sandbox requirements.

Therefore, a missed-call trigger may successfully create the lead while the WhatsApp send operation can fail because the recipient has not joined or initiated the required sandbox interaction.

The application handles this failure and returns a warning instead of treating the entire missed-call operation as a failure.

For production, a properly configured WhatsApp sender should be used instead of relying on the development sandbox.

---

# 🚨 Error Handling

The application uses centralized exception handling through:

```text
GlobalExceptionHandler
```

Handled exceptions include:

```text
ResourceAlreadyExistsException
ResourceNotFoundException
InvalidCredentialsException
MethodArgumentNotValidException
```

A standard error response is returned through:

```text
ErrorResponse
```

This keeps API error responses consistent.

---

# 🔒 CORS

The backend currently allows requests from:

```text
http://localhost:5173
```

This is suitable for the local React/Vite dashboard during development.

For production deployment, the allowed frontend origin should be updated to the deployed frontend URL.

---

# 🧩 API Summary

| Module | Method | Endpoint | Purpose |
|---|---|---|---|
| Auth | POST | `/api/v1/auth/register` | Register clinic and owner |
| Auth | POST | `/api/v1/auth/login` | Login |
| Auth | GET | `/api/v1/auth/me` | Get current user |
| Test | GET | `/api/v1/test` | Test JWT authentication |
| Clinic | GET | `/api/v1/clinic` | Get clinic |
| Clinic | PUT | `/api/v1/clinic` | Update clinic |
| Leads | GET | `/api/v1/leads` | Get clinic leads |
| Leads | GET | `/api/v1/leads/{id}` | Get lead |
| Leads | POST | `/api/v1/leads/demo` | Create demo lead |
| Leads | POST | `/api/v1/leads/{id}/confirm` | Confirm lead |
| Leads | POST | `/api/v1/leads/{id}/dismiss` | Dismiss lead |
| Missed Calls | POST | `/api/v1/missed-calls` | Create missed-call lead |
| Appointments | GET | `/api/v1/appointments` | Get appointments |
| Dashboard | GET | `/api/v1/dashboard` | Get dashboard summary |
| WhatsApp | POST | `/api/v1/whatsapp/webhook` | Receive WhatsApp messages |

---

# 🧱 Core Services

The backend is organized into feature-based modules.

### Authentication

```text
AuthController
AuthService
CustomUserDetailsService
JwtService
JwtAuthenticationFilter
```

### Lead Management

```text
LeadController
LeadService
LeadRepository
Lead
LeadStatus
```

### Appointment Management

```text
AppointmentController
AppointmentService
AppointmentRepository
Appointment
AppointmentStatus
```

### Conversation Management

```text
ConversationService
ConversationRepository
MessageRepository
Conversation
Message
ConversationState
SenderType
```

### WhatsApp

```text
WhatsAppService
WhatsAppServiceImpl
TwilioConfig
WhatsAppWebhookController
```

### Dashboard

```text
DashboardService
DashboardServiceImpl
DashboardSummaryResponse
```

---

# 📌 Important Design Decisions

### Stateless Authentication

The API uses JWT authentication instead of server-side sessions.

### Password Security

Passwords are never stored directly. They are hashed using BCrypt.

### Database Validation

Hibernate uses:

```text
ddl-auto: validate
```

while Flyway manages schema changes.

### Clinic-Level Isolation

Every authenticated request resolves the clinic from the authenticated user rather than trusting a clinic ID supplied by the client.

### UUID Identifiers

Entities use UUID-based identifiers for:

```text
Clinic
User
Lead
Conversation
Message
Appointment
```

---

# 📈 Future Improvements

Potential future improvements include:

- Production WhatsApp Cloud API integration
- Production deployment
- HTTPS
- Refresh tokens
- Role-based endpoint restrictions
- Appointment rescheduling
- Appointment cancellation
- Automated appointment reminders
- Better conversation intelligence
- AI-powered intent detection
- Analytics and reporting
- React dashboard integration
- Redis-based conversation/session support
- Automated tests
- CI/CD pipeline
- Monitoring and logging
- Rate limiting
- Production database backups

---

# 🧪 Example End-to-End Scenario

### Step 1 — Patient Misses a Call

A patient calls a dental clinic but the call is missed.

```text
Patient → Clinic
        → Missed Call
```

---

### Step 2 — Lead Is Created

The backend receives:

```http
POST /api/v1/missed-calls
```

with:

```json
{
  "patientPhone": "9876543210"
}
```

A lead is created for the authenticated clinic.

---

### Step 3 — WhatsApp Conversation Starts

DentalBot attempts to send a WhatsApp message.

The conversation is created with an initial state.

---

### Step 4 — Patient Provides Details

The bot collects:

```text
Service
Patient Name
Preferred Time
```

---

### Step 5 — Lead Waits for Confirmation

The lead moves to:

```text
AWAITING_CONFIRMATION
```

---

### Step 6 — Clinic Confirms

Clinic staff sends:

```http
POST /api/v1/leads/{id}/confirm
```

The lead becomes:

```text
CONFIRMED
```

and an appointment is created.

---

### Step 7 — Dashboard Updates

The dashboard reflects:

```text
Confirmed Leads
Today's Appointments
Recent Leads
```

---

# 🗃️ Useful PostgreSQL Commands

Connect to PostgreSQL:

```bash
docker exec -it dentalbot-postgres psql -U postgres -d dentalbot
```

List tables:

```sql
\dt
```

View clinics:

```sql
SELECT * FROM clinic;
```

View users:

```sql
SELECT * FROM users;
```

View leads:

```sql
SELECT * FROM lead;
```

View conversations:

```sql
SELECT * FROM conversation;
```

View messages:

```sql
SELECT * FROM message;
```

View appointments:

```sql
SELECT * FROM appointment;
```

Exit PostgreSQL:

```sql
\q
```

---

# 🐳 Useful Docker Commands

### Start services

```bash
docker compose up -d
```

### View containers

```bash
docker ps
```

### View logs

```bash
docker compose logs -f
```

### View PostgreSQL logs

```bash
docker compose logs -f postgres
```

### Stop services

```bash
docker compose down
```

### Stop and delete volumes

```bash
docker compose down -v
```

---

# 📜 License

This project is developed as a learning and portfolio project.

---

# 👩‍💻 Author

**Riya Nikam**

Electronics & Communication Engineering

---

## ⭐ Project Summary

DentalBot demonstrates a complete backend workflow for a multi-tenant dental clinic SaaS application:

```text
Authentication
      +
Multi-Tenancy
      +
Missed Call Recovery
      +
WhatsApp Conversations
      +
Lead Management
      +
Appointment Management
      +
Dashboard
      +
PostgreSQL
      +
Docker
      +
Flyway
      +
JWT Security
```

The project focuses on building a production-oriented Spring Boot backend with secure authentication, clinic-level data isolation, conversational lead recovery, and appointment management.