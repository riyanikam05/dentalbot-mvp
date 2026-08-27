package com.riya.dentalbot.appointment.service;

import com.riya.dentalbot.appointment.dto.AppointmentResponse;
import com.riya.dentalbot.lead.entity.Lead;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {

    AppointmentResponse createFromLead(
            Lead lead,
            String patientName,
            String service,
            LocalDateTime scheduledAt);

    List<AppointmentResponse> getAllAppointments();

}