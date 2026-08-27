package com.riya.dentalbot.appointment.repository;

import com.riya.dentalbot.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findAllByClinicIdOrderByScheduledAtDesc(UUID clinicId);

    Optional<Appointment> findByLeadId(UUID leadId);

    List<Appointment> findAllByClinicIdAndScheduledAtBetweenOrderByScheduledAtAsc(
            UUID clinicId, LocalDateTime start, LocalDateTime end);

}