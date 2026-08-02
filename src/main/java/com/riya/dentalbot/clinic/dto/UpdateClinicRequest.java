package com.riya.dentalbot.clinic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateClinicRequest(

        @NotBlank @Size(max = 100) String name,

        @NotBlank @Size(max = 20) String phone,

        @NotBlank @Size(max = 255) String address

) {
}