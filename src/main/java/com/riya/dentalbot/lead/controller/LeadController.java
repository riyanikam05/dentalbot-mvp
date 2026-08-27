package com.riya.dentalbot.lead.controller;

import com.riya.dentalbot.lead.dto.ConfirmLeadRequest;
import com.riya.dentalbot.lead.dto.LeadResponse;
import com.riya.dentalbot.lead.service.LeadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    @GetMapping
    public ResponseEntity<List<LeadResponse>> getAllLeads() {
        return ResponseEntity.ok(
                leadService.getAllLeads());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeadResponse> getLeadById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                leadService.getLeadById(id));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<LeadResponse> confirmLead(
            @PathVariable UUID id,
            @Valid @RequestBody ConfirmLeadRequest request) {

        return ResponseEntity.ok(
                leadService.confirmLead(id, request));
    }

    @PostMapping("/{id}/dismiss")
    public ResponseEntity<LeadResponse> dismissLead(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                leadService.dismissLead(id));
    }
}