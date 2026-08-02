package com.riya.dentalbot.clinic.repository;

import com.riya.dentalbot.clinic.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClinicRepository extends JpaRepository<Clinic, UUID> {

    Optional<Clinic> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

}