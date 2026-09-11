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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/missed-calls")
@RequiredArgsConstructor
@Slf4j
public class MissedCallController {

        private final ConversationService conversationService;
        private final WhatsAppService whatsAppService;
        private final UserRepository userRepository;
        private final ClinicRepository clinicRepository;

        @PostMapping
        public ResponseEntity<?> triggerMissedCall(
                        @Valid @RequestBody TriggerMissedCallRequest request) {

                Clinic clinic = getAuthenticatedClinic();

                String openingMessage = conversationService.startConversation(
                                clinic.getId(), request.patientPhone());

                try {

                        whatsAppService.sendMessage(request.patientPhone(), openingMessage);

                        return ResponseEntity.ok(Map.of(
                                        "leadCreated", true,
                                        "whatsappSent", true));

                } catch (Exception ex) {

                        log.warn(
                                        "Lead created for {} but WhatsApp message could not be sent: {}",
                                        request.patientPhone(),
                                        ex.getMessage());

                        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                                        "leadCreated", true,
                                        "whatsappSent", false,
                                        "warning",
                                        "Lead created, but the WhatsApp message could not be sent automatically. "
                                                        + "This is a known Twilio Sandbox restriction on business-initiated messages — "
                                                        + "ask the patient to message your WhatsApp number first, then retry."));
                }
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