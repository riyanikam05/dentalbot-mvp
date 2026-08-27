package com.riya.dentalbot.whatsapp.controller;

import com.riya.dentalbot.conversation.service.ConversationService;
import com.riya.dentalbot.util.PhoneNumberUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WhatsAppWebhookController {

    private final ConversationService conversationService;

    @PostMapping(value = "/api/v1/whatsapp/webhook", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.TEXT_XML_VALUE)
    public ResponseEntity<String> receiveMessage(
            @RequestParam("From") String from,
            @RequestParam("Body") String body) {

        String patientPhone = PhoneNumberUtil.fromWhatsAppFormat(from);

        Optional<UUID> clinicId = conversationService
                .resolveClinicIdForPatientPhone(patientPhone);

        if (clinicId.isEmpty()) {
            log.warn("No conversation found for inbound WhatsApp message from {}", patientPhone);
            return ResponseEntity.ok(emptyTwiml());
        }

        String reply = conversationService.handleIncomingMessage(
                clinicId.get(), patientPhone, body);

        if (reply == null) {
            return ResponseEntity.ok(emptyTwiml());
        }

        return ResponseEntity.ok(twimlReply(reply));
    }

    private String twimlReply(String message) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<Response><Message>" + escapeXml(message) + "</Message></Response>";
    }

    private String emptyTwiml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Response></Response>";
    }

    private String escapeXml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}