package com.riya.dentalbot.clinic.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record ClinicResponse(

        UUID id,

        String name,

        String email,

        String phone,

        String address,

        String city,

        LocalTime workingHoursStart,

        LocalTime workingHoursEnd,

        LocalDateTime createdAt

) {
}