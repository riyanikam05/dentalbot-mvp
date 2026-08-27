package com.riya.dentalbot.missedcall.controller;

import com.riya.dentalbot.clinic.entity.Clinic;
import com.riya.dentalbot.clinic.repository.ClinicRepository;
import com.riya.dentalbot.conversation.service.ConversationService;
import com.riya.dentalbot.exception.ResourceNotFoundException;
import com.riya.dentalbot.missedcall.dto.TriggerMissedCallRequest;
import com.riya.dentalbot.user.entity.User;
import com.riya.dentalbot.user.repository.UserRepository;
import com.riya.dentalbot.whatsapp.service.WhatsAppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/missed-calls")
@RequiredArgsConstructor
public class MissedCallController {

    private final ConversationService conversationService;
    private final WhatsAppService whatsAppService;
    private final UserRepository userRepository;
    private final ClinicRepository clinicRepository;

    @PostMapping
    public ResponseEntity<Void> triggerMissedCall(
            @Valid @RequestBody TriggerMissedCallRequest request) {

        Clinic clinic = getAuthenticatedClinic();

        String openingMessage = conversationService.startConversation(
                clinic.getId(), request.patientPhone());

        whatsAppService.sendMessage(request.patientPhone(), openingMessage);

        return ResponseEntity.ok().build();
    }

    private Clinic getAuthenticatedClinic() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return clinicRepository.findById(user.getClinicId())
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found"));
    }
}