CREATE TABLE clinic (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    working_hours_start TIME NOT NULL,
    working_hours_end TIME NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    clinic_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
        CHECK (role IN ('OWNER', 'RECEPTIONIST')),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT fk_user_clinic
        FOREIGN KEY (clinic_id)
        REFERENCES clinic(id)
        ON DELETE CASCADE
);

CREATE TABLE lead (
    id UUID PRIMARY KEY,
    clinic_id UUID NOT NULL,
    patient_phone VARCHAR(20) NOT NULL,
    patient_name VARCHAR(100),
    service_needed VARCHAR(100),
    preferred_time_text VARCHAR(255),
    status VARCHAR(50) NOT NULL
        CHECK (
            status IN (
                'AWAITING_REPLY',
                'COLLECTING_DETAILS',
                'AWAITING_CONFIRMATION',
                'CONFIRMED',
                'DISMISSED'
            )
        ),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT fk_lead_clinic
        FOREIGN KEY (clinic_id)
        REFERENCES clinic(id)
        ON DELETE CASCADE
);

CREATE TABLE conversation (
    id UUID PRIMARY KEY,
    clinic_id UUID NOT NULL,
    lead_id UUID NOT NULL UNIQUE,
    patient_phone VARCHAR(20) NOT NULL,
    state VARCHAR(50) NOT NULL
        CHECK (
            state IN (
                'ASKING_SERVICE',
                'ASKING_NAME',
                'ASKING_PREFERRED_TIME',
                'COMPLETED'
            )
        ),
    opted_out BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT fk_conversation_clinic
        FOREIGN KEY (clinic_id)
        REFERENCES clinic(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_conversation_lead
        FOREIGN KEY (lead_id)
        REFERENCES lead(id)
        ON DELETE CASCADE
);

CREATE TABLE message (
    id UUID PRIMARY KEY,
    conversation_id UUID NOT NULL,
    sender_type VARCHAR(20) NOT NULL
        CHECK (
            sender_type IN (
                'PATIENT',
                'BOT'
            )
        ),
    content TEXT NOT NULL,
    sent_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT fk_message_conversation
        FOREIGN KEY (conversation_id)
        REFERENCES conversation(id)
        ON DELETE CASCADE
);

CREATE TABLE appointment (
    id UUID PRIMARY KEY,
    clinic_id UUID NOT NULL,
    lead_id UUID NOT NULL UNIQUE,
    patient_name VARCHAR(100) NOT NULL,
    patient_phone VARCHAR(20) NOT NULL,
    service VARCHAR(100) NOT NULL,
    scheduled_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status VARCHAR(30) NOT NULL
        CHECK (
            status IN (
                'CONFIRMED',
                'COMPLETED',
                'CANCELLED'
            )
        ),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT fk_appointment_clinic
        FOREIGN KEY (clinic_id)
        REFERENCES clinic(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_appointment_lead
        FOREIGN KEY (lead_id)
        REFERENCES lead(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_users_clinic_id
    ON users(clinic_id);

CREATE INDEX idx_lead_clinic_id
    ON lead(clinic_id);

CREATE INDEX idx_conversation_clinic_id
    ON conversation(clinic_id);

CREATE INDEX idx_message_conversation_id
    ON message(conversation_id);

CREATE INDEX idx_appointment_clinic_id
    ON appointment(clinic_id);

CREATE INDEX idx_lead_status
    ON lead(status);

CREATE INDEX idx_lead_patient_phone
    ON lead(patient_phone);

CREATE INDEX idx_appointment_scheduled_at
    ON appointment(scheduled_at);

CREATE INDEX idx_message_sent_at
    ON message(sent_at);