package com.riya.dentalbot.lead.dto;

import com.riya.dentalbot.lead.entity.LeadStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateLeadStatusRequest(

        @NotNull LeadStatus status

) {
}