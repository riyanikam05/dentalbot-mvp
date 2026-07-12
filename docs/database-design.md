# Database Design

```mermaid
erDiagram
    CLINIC ||--o{ USER : has
    CLINIC ||--o{ LEAD : has
    CLINIC ||--o{ CONVERSATION : has
    CLINIC ||--o{ APPOINTMENT : has

    LEAD ||--o| CONVERSATION : has
    LEAD ||--o| APPOINTMENT : creates
    CONVERSATION ||--o{ MESSAGE : contains

CLINIC {
    uuid id PK
    string name
    string email
    string phone
    string address
    string city
    time working_hours_start
    time working_hours_end
    timestamp created_at
    timestamp updated_at
}

    USER {
    uuid id PK
    uuid clinic_id FK
    string name
    string email
    string password_hash
    string role
    timestamp created_at
    timestamp updated_at
}

    LEAD {
    uuid id PK
    uuid clinic_id FK
    string patient_phone
    string patient_name
    string service_needed
    string preferred_time_text
    string status
    timestamp created_at
    timestamp updated_at
}

    CONVERSATION {
    uuid id PK
    uuid clinic_id FK
    uuid lead_id FK
    string patient_phone
    string state
    boolean opted_out
    timestamp created_at
    timestamp updated_at
}

    MESSAGE {
    uuid id PK
    uuid conversation_id FK
    string sender_type
    string content
    timestamp sent_at
}

    APPOINTMENT {
    uuid id PK
    uuid clinic_id FK
    uuid lead_id FK
    string patient_name
    string patient_phone
    string service
    timestamp scheduled_at
    string status
    timestamp created_at
    timestamp updated_at
}
```

## Important Constraints

- `USER.email` must be unique.
- `CLINIC.phone` must be unique.
- `(clinic_id, patient_phone)` is not unique because the same patient can create multiple leads over time.
- `APPOINTMENT.lead_id` must be unique, ensuring one lead creates at most one appointment.
- `CONVERSATION.lead_id` must be unique in the MVP, ensuring one active conversation per lead.
- Every clinic-owned query must filter by `clinic_id`.