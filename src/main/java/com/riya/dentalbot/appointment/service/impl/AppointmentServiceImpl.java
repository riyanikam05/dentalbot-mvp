package com.riya.dentalbot.appointment.service.impl;

import com.riya.dentalbot.appointment.dto.AppointmentResponse;
import com.riya.dentalbot.appointment.entity.Appointment;
import com.riya.dentalbot.appointment.enums.AppointmentStatus;
import com.riya.dentalbot.appointment.repository.AppointmentRepository;
import com.riya.dentalbot.appointment.service.AppointmentService;
import com.riya.dentalbot.clinic.entity.Clinic;
import com.riya.dentalbot.clinic.repository.ClinicRepository;
import com.riya.dentalbot.exception.ResourceNotFoundException;
import com.riya.dentalbot.lead.entity.Lead;
import com.riya.dentalbot.user.entity.User;
import com.riya.dentalbot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ClinicRepository clinicRepository;
    private final UserRepository userRepository;

    @Override
    public AppointmentResponse createFromLead(
            Lead lead,
            String patientName,
            String service,
            LocalDateTime scheduledAt) {

        Appointment appointment = Appointment.builder()
                .id(UUID.randomUUID())
                .clinicId(lead.getClinicId())
                .leadId(lead.getId())
                .patientName(patientName)
                .patientPhone(lead.getPatientPhone())
                .service(service)
                .scheduledAt(scheduledAt)
                .status(AppointmentStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return mapToResponse(appointmentRepository.save(appointment));
    }

    @Override
    public List<AppointmentResponse> getAllAppointments() {

        Clinic clinic = getAuthenticatedClinic();

        return appointmentRepository
                .findAllByClinicIdOrderByScheduledAtDesc(clinic.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private Clinic getAuthenticatedClinic() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return clinicRepository.findById(user.getClinicId())
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found"));
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {

        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatientName(),
                appointment.getPatientPhone(),
                appointment.getService(),
                appointment.getScheduledAt(),
                appointment.getStatus(),
                appointment.getCreatedAt());
    }
}