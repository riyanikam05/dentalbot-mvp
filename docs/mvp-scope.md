# MVP Scope

Product statement:
DentalBot is a multi-tenant missed-call recovery MVP for dental clinics. When a clinic misses a patient call, the system creates a lead, starts a WhatsApp-style conversation to collect appointment details, and allows clinic staff to confirm an appointment from a dashboard.

Users:
1. Clinic owner
   - Registers the clinic
   - Views leads and appointments
   - Updates clinic details

2. Receptionist
   - Views missed-call leads
   - Reads patient conversations
   - Confirms or dismisses appointments

3. Patient
   - Calls the clinic, but the clinic does not answer
   - Receives a follow-up message
   - Shares service, name, and preferred appointment time

Main user journey:
Patient calls clinic
→ Clinic does not answer
→ DentalBot receives a missed-call event
→ DentalBot creates a lead and conversation
→ DentalBot sends the first follow-up message
→ patient replies
→ DentalBot collects service, name, and preferred time
→ Lead becomes AWAITING_CONFIRMATION
→ receptionist confirms appointment
→ DentalBot creates appointment
→ DentalBot stores confirmation message

Scope boundary:

Included:

- Clinic registration and login
- JWT authentication
- Clinic-isolated data using clinic_id
- Missed-call simulation endpoint
- Lead creation
- Conversation and message storage
- Rule-based patient detail collection
- Lead inbox
- Appointment confirmation
- Basic analytics

Not included:

- Stripe billing
- Redis
- Automated reminders
- Claude AI integration
- Real WhatsApp/Twilio integration initially
- Advanced staff management
- Calendar UI
- Deployment pipeline