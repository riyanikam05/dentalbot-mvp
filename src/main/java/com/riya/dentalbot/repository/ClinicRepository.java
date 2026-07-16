package com.riya.dentalbot.repository;

import com.riya.dentalbot.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClinicRepository extends JpaRepository<Clinic, UUID> {

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

}