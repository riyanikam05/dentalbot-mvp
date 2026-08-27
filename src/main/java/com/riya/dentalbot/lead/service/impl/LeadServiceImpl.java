package com.riya.dentalbot.lead.service.impl;

import com.riya.dentalbot.appointment.service.AppointmentService;
import com.riya.dentalbot.clinic.entity.Clinic;
import com.riya.dentalbot.clinic.repository.ClinicRepository;
import com.riya.dentalbot.exception.ResourceNotFoundException;
import com.riya.dentalbot.lead.dto.ConfirmLeadRequest;
import com.riya.dentalbot.lead.dto.LeadResponse;
import com.riya.dentalbot.lead.entity.Lead;
import com.riya.dentalbot.lead.entity.LeadStatus;
import com.riya.dentalbot.lead.repository.LeadRepository;
import com.riya.dentalbot.lead.service.LeadService;
import com.riya.dentalbot.user.entity.User;
import com.riya.dentalbot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final ClinicRepository clinicRepository;
    private final AppointmentService appointmentService;

    @Override
    public List<LeadResponse> getAllLeads() {

        Clinic clinic = getAuthenticatedClinic();

        return leadRepository
                .findAllByClinicIdOrderByCreatedAtDesc(clinic.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public LeadResponse getLeadById(UUID id) {

        Clinic clinic = getAuthenticatedClinic();

        Lead lead = leadRepository
                .findByIdAndClinicId(id, clinic.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found"));

        return mapToResponse(lead);
    }

    @Override
    @Transactional
    public Lead findOrCreateActiveLead(UUID clinicId, String patientPhone) {

        return leadRepository
                .findFirstByClinicIdAndPatientPhoneAndStatusNotInOrderByCreatedAtDesc(
                        clinicId,
                        patientPhone,
                        List.of(
                                LeadStatus.CONFIRMED,
                                LeadStatus.DISMISSED))
                .orElseGet(() -> createNewLead(clinicId, patientPhone));
    }

    @Override
    @Transactional
    public Lead createNewLead(UUID clinicId, String patientPhone) {

        Lead lead = Lead.builder()
                .id(UUID.randomUUID())
                .clinicId(clinicId)
                .patientPhone(patientPhone)
                .status(LeadStatus.AWAITING_REPLY)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return leadRepository.save(lead);
    }

    @Override
    @Transactional
    public Lead updateCollectedDetails(
            UUID leadId,
            String serviceNeeded,
            String patientName,
            String preferredTimeText) {

        Lead lead = leadRepository
                .findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found"));

        if (serviceNeeded != null) {
            lead.setServiceNeeded(serviceNeeded);
        }

        if (patientName != null) {
            lead.setPatientName(patientName);
        }

        if (preferredTimeText != null) {
            lead.setPreferredTimeText(preferredTimeText);
        }

        lead.setStatus(LeadStatus.COLLECTING_DETAILS);
        lead.setUpdatedAt(LocalDateTime.now());

        return leadRepository.save(lead);
    }

    @Override
    @Transactional
    public Lead markAwaitingConfirmation(UUID leadId) {

        Lead lead = leadRepository
                .findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found"));

        lead.setStatus(LeadStatus.AWAITING_CONFIRMATION);
        lead.setUpdatedAt(LocalDateTime.now());

        return leadRepository.save(lead);
    }

    @Override
    @Transactional
    public LeadResponse confirmLead(
            UUID id,
            ConfirmLeadRequest request) {

        Clinic clinic = getAuthenticatedClinic();

        Lead lead = leadRepository
                .findByIdAndClinicId(id, clinic.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found"));

        lead.setPatientName(request.patientName());
        lead.setServiceNeeded(request.service());
        lead.setStatus(LeadStatus.CONFIRMED);
        lead.setUpdatedAt(LocalDateTime.now());

        Lead savedLead = leadRepository.save(lead);

        appointmentService.createFromLead(
                savedLead,
                request.patientName(),
                request.service(),
                request.scheduledAt());

        return mapToResponse(savedLead);
    }

    @Override
    @Transactional
    public LeadResponse dismissLead(UUID id) {

        Clinic clinic = getAuthenticatedClinic();

        Lead lead = leadRepository
                .findByIdAndClinicId(id, clinic.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found"));

        lead.setStatus(LeadStatus.DISMISSED);
        lead.setUpdatedAt(LocalDateTime.now());

        Lead savedLead = leadRepository.save(lead);

        return mapToResponse(savedLead);
    }

    private Clinic getAuthenticatedClinic() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return clinicRepository
                .findById(user.getClinicId())
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found"));
    }

    private LeadResponse mapToResponse(Lead lead) {

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