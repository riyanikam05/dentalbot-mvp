# API Contracts

Base URL: `/api`

## Authentication

Protected endpoints require:

```http
Authorization: Bearer <JWT_TOKEN>
```

## Endpoints

| Method | Endpoint | Purpose | Authentication |
|---|---|---|---|
| POST | `/auth/register` | Register a clinic and its owner account | No |
| POST | `/auth/login` | Authenticate owner and return JWT | No |
| POST | `/webhooks/missed-call` | Simulate a missed call and create a lead | Protected test endpoint |
| POST | `/conversations/{id}/reply` | Simulate a patient reply | Protected test endpoint |
| GET | `/leads` | List leads for the logged-in clinic | Yes |
| GET | `/leads/{id}` | View one lead | Yes |
| GET | `/conversations/{id}` | View messages for one conversation | Yes |
| PUT | `/leads/{id}/confirm` | Confirm an appointment for a lead | Yes |
| PUT | `/leads/{id}/dismiss` | Mark a lead as dismissed | Yes |
| GET | `/analytics/dashboard` | Return dashboard metrics | Yes |

## Request and Response Examples

### Register Clinic

**POST** `/api/auth/register`

```json
{
  "clinicName": "SmileCare Dental Clinic",
  "ownerName": "Dr. Ananya Sharma",
  "email": "owner@smilecare.example",
  "password": "ExamplePassword@123",
  "phone": "+919876543210",
  "address": "Dharampeth, Nagpur",
  "city": "Nagpur"
}
```

**201 Created**

```json
{
  "clinicId": "c8c57c31-6d1f-4c0c-9df4-2bca6d5d3b6f",
  "userId": "e2c0c4de-4b1f-43bf-9b5d-b4d1a4d0b72e",
  "message": "Clinic registered successfully"
}
```

### Simulate Missed Call

**POST** `/api/webhooks/missed-call`

```json
{
  "clinicPhone": "+919876543210",
  "patientPhone": "+919812345678"
}
```

**201 Created**

```json
{
  "leadId": "d3d5f5e4-22a7-42bb-9f4e-80a69d8f47aa",
  "conversationId": "c68dbb22-2a2a-4f1c-a4b0-1fd9a8d6b59c",
  "status": "NEW",
  "message": "Missed call recorded and lead created"
}
```

### Confirm Appointment

**PUT** `/api/leads/{id}/confirm`

```json
{
  "scheduledAt": "2026-08-15T17:00:00"
}
```

**200 OK**

```json
{
  "appointmentId": "a71d4e84-3f51-4c35-9a46-b0f10c1d2d8c",
  "leadId": "d3d5f5e4-22a7-42bb-9f4e-80a69d8f47aa",
  "scheduledAt": "2026-08-15T17:00:00",
  "status": "CONFIRMED"
}
```

### Dashboard Analytics

**GET** `/api/analytics/dashboard`

**200 OK**

```json
{
  "todayLeads": 10,
  "awaitingConfirmation": 3,
  "confirmed": 5,
  "dismissed": 2,
  "appointments": 5
}
```

## Error Handling

| Status | Meaning |
|---|---|
| `400 Bad Request` | Invalid request body, missing required fields, or invalid appointment time |
| `401 Unauthorized` | Missing or invalid JWT |
| `403 Forbidden` | User attempts to access data belonging to another clinic |
| `404 Not Found` | Clinic, lead, or conversation does not exist |
| `409 Conflict` | Duplicate missed call or an appointment already exists for the lead |