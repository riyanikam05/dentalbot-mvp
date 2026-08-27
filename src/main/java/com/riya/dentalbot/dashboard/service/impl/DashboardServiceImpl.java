package com.riya.dentalbot.dashboard.service.impl;

import com.riya.dentalbot.appointment.dto.AppointmentResponse;
import com.riya.dentalbot.appointment.entity.Appointment;
import com.riya.dentalbot.appointment.repository.AppointmentRepository;
import com.riya.dentalbot.clinic.entity.Clinic;
import com.riya.dentalbot.clinic.repository.ClinicRepository;
import com.riya.dentalbot.dashboard.dto.DashboardSummaryResponse;
import com.riya.dentalbot.dashboard.service.DashboardService;
import com.riya.dentalbot.exception.ResourceNotFoundException;
import com.riya.dentalbot.lead.dto.LeadResponse;
import com.riya.dentalbot.lead.entity.Lead;
import com.riya.dentalbot.lead.entity.LeadStatus;
import com.riya.dentalbot.lead.repository.LeadRepository;
import com.riya.dentalbot.user.entity.User;
import com.riya.dentalbot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final LeadRepository leadRepository;
    private final AppointmentRepository appointmentRepository;
    private final ClinicRepository clinicRepository;
    private final UserRepository userRepository;

    @Override
    public DashboardSummaryResponse getSummary() {

        UUID clinicId = getAuthenticatedClinicId();

        long totalLeads = leadRepository
                .findAllByClinicIdOrderByCreatedAtDesc(clinicId)
                .size();

        long awaitingReply = leadRepository.countByClinicIdAndStatus(
                clinicId, LeadStatus.AWAITING_REPLY);

        long collectingDetails = leadRepository.countByClinicIdAndStatus(
                clinicId, LeadStatus.COLLECTING_DETAILS);

        long awaitingConfirmation = leadRepository.countByClinicIdAndStatus(
                clinicId, LeadStatus.AWAITING_CONFIRMATION);

        long confirmed = leadRepository.countByClinicIdAndStatus(
                clinicId, LeadStatus.CONFIRMED);

        long dismissed = leadRepository.countByClinicIdAndStatus(
                clinicId, LeadStatus.DISMISSED);

        LocalDateTime startOfDay = LocalDate.now().atTime(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        List<AppointmentResponse> todaysAppointments = appointmentRepository
                .findAllByClinicIdAndScheduledAtBetweenOrderByScheduledAtAsc(
                        clinicId, startOfDay, endOfDay)
                .stream()
                .map(this::mapAppointment)
                .toList();

        List<LeadResponse> recentLeads = leadRepository
                .findTop5ByClinicIdOrderByCreatedAtDesc(clinicId)
                .stream()
                .map(this::mapLead)
                .toList();

        return new DashboardSummaryResponse(
                totalLeads,
                awaitingReply,
                collectingDetails,
                awaitingConfirmation,
                confirmed,
                dismissed,
                todaysAppointments,
                recentLeads);
    }

    private UUID getAuthenticatedClinicId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Clinic clinic = clinicRepository.findById(user.getClinicId())
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found"));

        return clinic.getId();
    }

    private AppointmentResponse mapAppointment(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatientName(),
                appointment.getPatientPhone(),
                appointment.getService(),
                appointment.getScheduledAt(),
                appointment.getStatus(),
                appointment.getCreatedAt());
    }

    private LeadResponse mapLead(Lead lead) {
        return new LeadResponse(
                lead.getId(),
                lead.getPatientPhone(),
                lead.getPatientName(),
                lead.getServiceNeeded(),
                lead.getPreferredTimeText(),
                lead.getStatus(),
                lead.getCreatedAt(),
                lead.getUpdatedAt());
    }
}