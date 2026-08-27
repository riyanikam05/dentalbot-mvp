package com.riya.dentalbot.dashboard.dto;

import com.riya.dentalbot.appointment.dto.AppointmentResponse;
import com.riya.dentalbot.lead.dto.LeadResponse;

import java.util.List;

public record DashboardSummaryResponse(

        long totalLeads,
        long awaitingReplyCount,
        long collectingDetailsCount,
        long awaitingConfirmationCount,
        long confirmedCount,
        long dismissedCount,

        List<AppointmentResponse> todaysAppointments,
        List<LeadResponse> recentLeads

) {
}