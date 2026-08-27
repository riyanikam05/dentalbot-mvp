package com.riya.dentalbot.lead.repository;

import com.riya.dentalbot.lead.entity.Lead;
import com.riya.dentalbot.lead.entity.LeadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeadRepository extends JpaRepository<Lead, UUID> {

    List<Lead> findAllByClinicIdOrderByCreatedAtDesc(UUID clinicId);

    Optional<Lead> findByIdAndClinicId(UUID id, UUID clinicId);

    Optional<Lead> findFirstByClinicIdAndPatientPhoneAndStatusNotInOrderByCreatedAtDesc(
            UUID clinicId, String patientPhone, List<LeadStatus> excludedStatuses);

    long countByClinicIdAndStatus(UUID clinicId, LeadStatus status);

    List<Lead> findTop5ByClinicIdOrderByCreatedAtDesc(UUID clinicId);

}