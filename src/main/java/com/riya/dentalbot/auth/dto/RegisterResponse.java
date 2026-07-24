package com.riya.dentalbot.auth.dto;

import java.util.UUID;

public record RegisterResponse(

        UUID clinicId,
        UUID userId,
        String message

) {}