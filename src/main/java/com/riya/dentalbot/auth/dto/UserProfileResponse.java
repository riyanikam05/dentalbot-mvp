package com.riya.dentalbot.auth.dto;

import com.riya.dentalbot.user.enums.Role;

import java.util.UUID;

public record UserProfileResponse(

        UUID userId,
        UUID clinicId,
        String name,
        String email,
        Role role

) {}