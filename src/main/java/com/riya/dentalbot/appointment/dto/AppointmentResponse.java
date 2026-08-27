package com.riya.dentalbot.appointment.dto;

import com.riya.dentalbot.appointment.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponse(

        UUID id,
        String patientName,
        String patientPhone,
        String service,
        LocalDateTime scheduledAt,
        AppointmentStatus status,
        LocalDateTime createdAt

) {
}