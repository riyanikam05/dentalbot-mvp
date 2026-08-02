package com.riya.dentalbot.lead.repository;

import com.riya.dentalbot.lead.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeadRepository extends JpaRepository<Lead, UUID> {

    List<Lead> findAllByClinicIdOrderByCreatedAtDesc(UUID clinicId);

    Optional<Lead> findByIdAndClinicId(UUID id, UUID clinicId);

}