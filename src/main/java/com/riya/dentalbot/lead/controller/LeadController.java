package com.riya.dentalbot.lead.controller;

import com.riya.dentalbot.lead.dto.LeadResponse;
import com.riya.dentalbot.lead.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}