package com.riya.dentalbot.lead.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ConfirmLeadRequest(

        @NotBlank String patientName,

        @NotBlank String service,

        @NotNull @Future LocalDateTime scheduledAt

) {
}