# Data Requirements

Clinic data:

Clinic
- id
- name
- email
- phone
- address
- city
- workingHoursStart
- workingHoursEnd
- createdAt
- updatedAt

time working_hours_start
time working_hours_end
timestamp created_at
timestamp updated_at

Why: DentalBot needs clinic identity, contact information, and address for appointment confirmation.

User data:
User
- id
- clinicId
- name
- email
- passwordHash
- role
- createdAt
- updatedAt

timestamp created_at
timestamp updated_at

Roles to be used:

OWNER
RECEPTIONIST

Why: Staff members log in, and every user belongs to one clinic.

Lead data:
Lead
- id
- clinicId
- patientPhone
- patientName
- serviceNeeded
- preferredTimeText
- status
- createdAt
- updatedAt
timestamp created_at
timestamp updated_at

Lead statuses to be used:

AWAITING_REPLY
COLLECTING_DETAILS
AWAITING_CONFIRMATION
CONFIRMED
DISMISSED

Why: a lead is the business record that moves from missed call to appointment.

Conversation data:
Conversation
- id
- clinicId
- leadId
- patientPhone
- state
- optedOut
- createdAt
- updatedAt
timestamp created_at
timestamp updated_at

ConversationState:

ASKING_SERVICE
ASKING_NAME
ASKING_PREFERRED_TIME
COMPLETED

Why: the conversation state tells your backend what question to ask next.

Message data:
Message
- id
- conversationId
- senderType
- content
- sentAt

timestamp sent_at

Sender types to be used:

PATIENT
BOT

Why: the receptionist needs to see the conversation history, and the system needs an audit trail.

Appointment data:
Appointment
- id
- clinicId
- leadId
- patientName
- patientPhone
- service
- scheduledAt
- status
- createdAt
- updatedAt

timestamp scheduled_at
timestamp created_at
timestamp updated_at

Appointment statuses to be used:

CONFIRMED
CANCELLED
COMPLETED

- Every lead, conversation, and appointment belongs to exactly one clinic.
- Every API query must filter by clinicId.
- A patient phone number can have multiple leads over time.
- One lead has one active conversation in the MVP.
- One confirmed lead can create at most one appointment.