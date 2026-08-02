package com.riya.dentalbot.lead.dto;

import com.riya.dentalbot.lead.entity.LeadStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record LeadResponse(

        UUID id,

        String patientPhone,

        String patientName,

        String serviceNeeded,

        String preferredTimeText,

        LeadStatus status,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}