package com.riya.dentalbot.controller;

import com.riya.dentalbot.dto.request.RegisterClinicRequest;
import com.riya.dentalbot.dto.response.RegisterClinicResponse;
import com.riya.dentalbot.service.ClinicService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clinics")
public class ClinicController {

    private final ClinicService clinicService;

    public ClinicController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterClinicResponse registerClinic(
            @Valid @RequestBody RegisterClinicRequest request) {

        return clinicService.registerClinic(request);
    }
}