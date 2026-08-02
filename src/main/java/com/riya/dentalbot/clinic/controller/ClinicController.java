package com.riya.dentalbot.clinic.controller;

import com.riya.dentalbot.clinic.dto.ClinicResponse;
import com.riya.dentalbot.clinic.dto.UpdateClinicRequest;
import com.riya.dentalbot.clinic.service.ClinicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clinic")
@RequiredArgsConstructor
public class ClinicController {

    private final ClinicService clinicService;

    @GetMapping
    public ResponseEntity<ClinicResponse> getClinicProfile() {

        return ResponseEntity.ok(
                clinicService.getClinicProfile());
    }

    @PutMapping
    public ResponseEntity<ClinicResponse> updateClinicProfile(
            @Valid @RequestBody UpdateClinicRequest request) {

        return ResponseEntity.ok(
                clinicService.updateClinicProfile(request));
    }
}