package com.riya.dentalbot.lead.service.impl;

import com.riya.dentalbot.clinic.entity.Clinic;
import com.riya.dentalbot.clinic.repository.ClinicRepository;
import com.riya.dentalbot.exception.ResourceNotFoundException;
import com.riya.dentalbot.lead.dto.LeadResponse;
import com.riya.dentalbot.lead.entity.Lead;
import com.riya.dentalbot.lead.repository.LeadRepository;
import com.riya.dentalbot.lead.service.LeadService;
import com.riya.dentalbot.user.entity.User;
import com.riya.dentalbot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final ClinicRepository clinicRepository;

    @Override
    public List<LeadResponse> getAllLeads() {

        Clinic clinic = getAuthenticatedClinic();

        return leadRepository
                .findAllByClinicIdOrderByCreatedAtDesc(clinic.getId())
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