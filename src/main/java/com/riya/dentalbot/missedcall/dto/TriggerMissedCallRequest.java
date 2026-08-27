package com.riya.dentalbot.missedcall.dto;

import jakarta.validation.constraints.Pattern;

public record TriggerMissedCallRequest(

        @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number")
        String patientPhone

) {
}