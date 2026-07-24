package com.riya.dentalbot.auth.service;

import com.riya.dentalbot.auth.dto.LoginRequest;
import com.riya.dentalbot.auth.dto.LoginResponse;
import com.riya.dentalbot.auth.dto.RegisterRequest;
import com.riya.dentalbot.auth.dto.RegisterResponse;
import com.riya.dentalbot.clinic.entity.Clinic;
import com.riya.dentalbot.clinic.repository.ClinicRepository;
import com.riya.dentalbot.user.entity.User;
import com.riya.dentalbot.user.enums.Role;
import com.riya.dentalbot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ClinicRepository clinicRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public RegisterResponse register(RegisterRequest request) {

        if (clinicRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Clinic email already exists.");
        }

        if (clinicRepository.existsByPhone(request.phone())) {
            throw new RuntimeException("Phone number already exists.");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("User email already exists.");
        }

        UUID clinicId = UUID.randomUUID();

        Clinic clinic = Clinic.builder()
                .id(clinicId)
                .name(request.clinicName())
                .email(request.email())
                .phone(request.phone())
                .address(request.address())
                .city(request.city())
                .workingHoursStart(LocalTime.of(9, 0))
                .workingHoursEnd(LocalTime.of(18, 0))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        clinicRepository.save(clinic);

        UUID userId = UUID.randomUUID();

        User owner = User.builder()
                .id(userId)
                .clinicId(clinicId)
                .name(request.ownerName())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.OWNER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(owner);

        return new RegisterResponse(
                clinicId,
                userId,
                "Clinic registered successfully."
        );
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        throw new UnsupportedOperationException("Login not implemented yet.");
    }
}