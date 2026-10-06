package com.riya.dentalbot.lead.service;

import com.riya.dentalbot.lead.dto.ConfirmLeadRequest;
import com.riya.dentalbot.lead.dto.LeadResponse;
import com.riya.dentalbot.lead.entity.Lead;

import java.util.List;
import java.util.UUID;

public interface LeadService {

    List<LeadResponse> getAllLeads();

    LeadResponse getLeadById(UUID id);

    Lead findOrCreateActiveLead(UUID clinicId, String patientPhone);

    Lead createNewLead(UUID clinicId, String patientPhone);

    Lead createDemoLead(
            String patientName,
            String patientPhone,
            String service,
            String preferredTime);

    Lead updateCollectedDetails(
            UUID leadId,
            String serviceNeeded,
            String patientName,
            String preferredTimeText);

    Lead markAwaitingConfirmation(UUID leadId);

    LeadResponse confirmLead(UUID id, ConfirmLeadRequest request);

    LeadResponse dismissLead(UUID id);
}