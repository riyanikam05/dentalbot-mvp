package com.riya.dentalbot.auth.service;

import com.riya.dentalbot.auth.dto.LoginRequest;
import com.riya.dentalbot.auth.dto.LoginResponse;
import com.riya.dentalbot.auth.dto.RegisterRequest;
import com.riya.dentalbot.auth.dto.RegisterResponse;
import com.riya.dentalbot.auth.dto.UserProfileResponse;
import com.riya.dentalbot.clinic.entity.Clinic;
import com.riya.dentalbot.clinic.repository.ClinicRepository;
import com.riya.dentalbot.exception.InvalidCredentialsException;
import com.riya.dentalbot.exception.ResourceAlreadyExistsException;
import com.riya.dentalbot.exception.ResourceNotFoundException;
import com.riya.dentalbot.user.entity.User;
import com.riya.dentalbot.user.enums.Role;
import com.riya.dentalbot.user.repository.UserRepository;
import com.riya.dentalbot.util.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public RegisterResponse register(RegisterRequest request) {

        if (clinicRepository.existsByEmail(request.email())) {
            throw new ResourceAlreadyExistsException(
                    "A clinic with this email already exists."
            );
        }

        if (clinicRepository.existsByPhone(request.phone())) {
            throw new ResourceAlreadyExistsException(
                    "A clinic with this phone number already exists."
            );
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new ResourceAlreadyExistsException(
                    "A user with this email already exists."
            );
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

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

        } catch (BadCredentialsException ex) {

            throw new InvalidCredentialsException(
                    "Invalid email or password."
            );
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(token);
    }

    @Override
    public UserProfileResponse getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        return new UserProfileResponse(
                user.getId(),
                user.getClinicId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}