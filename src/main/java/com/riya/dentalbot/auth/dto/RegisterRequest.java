package com.riya.dentalbot.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Clinic name is required")
        String clinicName,

        @NotBlank(message = "Owner name is required")
        String ownerName,

        @Email(message = "Invalid email")
        @NotBlank(message = "Email is required")
        String email,

        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @Pattern(
                regexp = "^[6-9]\\d{9}$",
                message = "Invalid phone number"
        )
        String phone,

        @NotBlank
        String address,

        @NotBlank
        String city

) {}