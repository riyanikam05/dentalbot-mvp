package com.riya.dentalbot.clinic.service;

import com.riya.dentalbot.clinic.entity.Clinic;
import com.riya.dentalbot.clinic.repository.ClinicRepository;
import com.riya.dentalbot.clinic.dto.ClinicResponse;
import com.riya.dentalbot.clinic.dto.UpdateClinicRequest;
import com.riya.dentalbot.exception.ResourceNotFoundException;
import com.riya.dentalbot.user.entity.User;
import com.riya.dentalbot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ClinicService {

    private final ClinicRepository clinicRepository;
    private final UserRepository userRepository;

    public ClinicResponse getClinicProfile() {

        Clinic clinic = getAuthenticatedClinic();

        return mapToResponse(clinic);
    }

    public ClinicResponse updateClinicProfile(UpdateClinicRequest request) {

        Clinic clinic = getAuthenticatedClinic();

        clinic.setName(request.name());
        clinic.setPhone(request.phone());
        clinic.setAddress(request.address());
        clinic.setUpdatedAt(LocalDateTime.now());

        Clinic updatedClinic = clinicRepository.save(clinic);

        return mapToResponse(updatedClinic);
    }

    private Clinic getAuthenticatedClinic() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return clinicRepository.findById(user.getClinicId())
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found"));
    }

    private ClinicResponse mapToResponse(Clinic clinic) {

        return new ClinicResponse(
                clinic.getId(),
                clinic.getName(),
                clinic.getEmail(),
                clinic.getPhone(),
                clinic.getAddress(),
                clinic.getCity(),
                clinic.getWorkingHoursStart(),
                clinic.getWorkingHoursEnd(),
                clinic.getCreatedAt());
    }
}