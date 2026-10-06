package com.riya.dentalbot.lead.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateLeadRequest(

                @NotBlank String patientName,

                @NotBlank String patientPhone,

                @NotBlank String service,

                @NotBlank String preferredTime) {
}