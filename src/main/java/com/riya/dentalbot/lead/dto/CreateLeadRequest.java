package com.riya.dentalbot.lead.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateLeadRequest(

        @NotBlank @Size(max = 20) String patientPhone,

        @Size(max = 100) String patientName,

        @Size(max = 100) String serviceNeeded,

        @Size(max = 100) String preferredTimeText

) {
}