package com.riya.dentalbot.service.impl;

import com.riya.dentalbot.dto.request.RegisterClinicRequest;
import com.riya.dentalbot.dto.response.RegisterClinicResponse;
import com.riya.dentalbot.entity.Clinic;
import com.riya.dentalbot.repository.ClinicRepository;
import com.riya.dentalbot.service.ClinicService;
import org.springframework.stereotype.Service;
import com.riya.dentalbot.exception.ResourceAlreadyExistsException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ClinicServiceImpl implements ClinicService {

    private final ClinicRepository clinicRepository;

    public ClinicServiceImpl(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    @Override
    public RegisterClinicResponse registerClinic(RegisterClinicRequest request) {

        if (clinicRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException(
                "Clinic with email '" + request.getEmail() + "' already exists.");
        }

        if (clinicRepository.existsByPhone(request.getPhone())) {
            throw new ResourceAlreadyExistsException(
                "Clinic with phone '" + request.getPhone() + "' already exists.");
        }

        if (clinicRepository.existsByPhone(request.getPhone())) {
            throw new ResourceAlreadyExistsException(
                "Clinic with phone '" + request.getPhone() + "' already exists.");
        }

        Clinic clinic = Clinic.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .workingHoursStart(request.getWorkingHoursStart())
                .workingHoursEnd(request.getWorkingHoursEnd())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        clinicRepository.save(clinic);

        return RegisterClinicResponse.builder()
                .id(clinic.getId())
                .name(clinic.getName())
                .email(clinic.getEmail())
                .phone(clinic.getPhone())
                .message("Clinic registered successfully")
                .build();
    }
}