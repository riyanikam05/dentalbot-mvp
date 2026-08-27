package com.riya.dentalbot.whatsapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MissedCallRequest(

        @NotNull UUID clinicId,

        @NotBlank String patientPhone

) {
}