package com.riya.dentalbot.conversation.service.impl;

import com.riya.dentalbot.conversation.entity.Conversation;
import com.riya.dentalbot.conversation.entity.Message;
import com.riya.dentalbot.conversation.enums.ConversationState;
import com.riya.dentalbot.conversation.enums.SenderType;
import com.riya.dentalbot.conversation.repository.ConversationRepository;
import com.riya.dentalbot.conversation.repository.MessageRepository;
import com.riya.dentalbot.conversation.service.ConversationService;
import com.riya.dentalbot.lead.entity.Lead;
import com.riya.dentalbot.lead.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

        private final ConversationRepository conversationRepository;
        private final MessageRepository messageRepository;
        private final LeadService leadService;

        private static final String SERVICE_QUESTION = "Hi! Thanks for reaching out. What treatment or service are you looking for?";

        private static final String NAME_QUESTION = "Got it. Could you share the patient's name?";

        private static final String TIME_QUESTION = "Thanks! What day/time works best for you?";

        private static final String COMPLETION_MESSAGE = "Perfect, thank you! Our team will call you shortly to confirm your appointment.";

        private static final String COMPLETED_MESSAGE = "Thanks for your patience — our team is reviewing your request and will call you shortly.";

        @Override
        @Transactional
        public String startConversation(UUID clinicId, String patientPhone) {

                Lead lead = leadService.findOrCreateActiveLead(
                                clinicId,
                                patientPhone);

                Conversation conversation = conversationRepository
                                .findByLeadId(lead.getId())
                                .orElseGet(() -> createConversation(
                                                clinicId,
                                                lead.getId(),
                                                patientPhone));

                if (conversation.getState() == ConversationState.COMPLETED) {

                        conversation.setState(ConversationState.ASKING_SERVICE);
                        conversation.setOptedOut(false);
                        conversation.setUpdatedAt(LocalDateTime.now());

                        conversationRepository.save(conversation);
                }

                saveMessage(
                                conversation.getId(),
                                SenderType.BOT,
                                SERVICE_QUESTION);

                return SERVICE_QUESTION;
        }

        @Override
        @Transactional
        public String handleIncomingMessage(
                        UUID clinicId,
                        String patientPhone,
                        String messageText) {

                Lead lead = leadService.findOrCreateActiveLead(
                                clinicId,
                                patientPhone);

                Optional<Conversation> existingConversation = conversationRepository.findByLeadId(lead.getId());

                Conversation conversation = existingConversation
                                .orElseGet(() -> createConversation(
                                                clinicId,
                                                lead.getId(),
                                                patientPhone));

                if (Boolean.TRUE.equals(conversation.getOptedOut())) {
                        return null;
                }

                String normalizedMessage = messageText == null
                                ? ""
                                : messageText.trim();

                if (isOptOutKeyword(normalizedMessage)) {

                        saveMessage(
                                        conversation.getId(),
                                        SenderType.PATIENT,
                                        normalizedMessage);

                        conversation.setOptedOut(true);
                        conversation.setUpdatedAt(LocalDateTime.now());

                        conversationRepository.save(conversation);

                        String reply = "You've been unsubscribed and won't receive further messages from us.";

                        saveMessage(
                                        conversation.getId(),
                                        SenderType.BOT,
                                        reply);

                        return reply;
                }

                if (isGreeting(normalizedMessage)) {

                        saveMessage(
                                        conversation.getId(),
                                        SenderType.PATIENT,
                                        normalizedMessage);

                        conversation.setState(ConversationState.ASKING_SERVICE);
                        conversation.setOptedOut(false);
                        conversation.setUpdatedAt(LocalDateTime.now());

                        conversationRepository.save(conversation);

                        saveMessage(
                                        conversation.getId(),
                                        SenderType.BOT,
                                        SERVICE_QUESTION);

                        return SERVICE_QUESTION;
                }

                saveMessage(
                                conversation.getId(),
                                SenderType.PATIENT,
                                normalizedMessage);

                if (existingConversation.isEmpty()) {

                        conversation.setState(ConversationState.ASKING_SERVICE);
                        conversation.setUpdatedAt(LocalDateTime.now());

                        conversationRepository.save(conversation);

                        saveMessage(
                                        conversation.getId(),
                                        SenderType.BOT,
                                        SERVICE_QUESTION);

                        return SERVICE_QUESTION;
                }

                String reply;

                switch (conversation.getState()) {

                        case ASKING_SERVICE -> {

                                leadService.updateCollectedDetails(
                                                lead.getId(),
                                                normalizedMessage,
                                                null,
                                                null);

                                conversation.setState(
                                                ConversationState.ASKING_NAME);

                                reply = NAME_QUESTION;
                        }

                        case ASKING_NAME -> {

                                leadService.updateCollectedDetails(
                                                lead.getId(),
                                                null,
                                                normalizedMessage,
                                                null);

                                conversation.setState(
                                                ConversationState.ASKING_PREFERRED_TIME);

                                reply = TIME_QUESTION;
                        }

                        case ASKING_PREFERRED_TIME -> {

                                leadService.updateCollectedDetails(
                                                lead.getId(),
                                                null,
                                                null,
                                                normalizedMessage);

                                leadService.markAwaitingConfirmation(
                                                lead.getId());

                                conversation.setState(
                                                ConversationState.COMPLETED);

                                reply = COMPLETION_MESSAGE;
                        }

                        case COMPLETED -> {

                                reply = COMPLETED_MESSAGE;
                        }

                        default -> {

                                conversation.setState(
                                                ConversationState.ASKING_SERVICE);

                                reply = SERVICE_QUESTION;
                        }
                }

                conversation.setUpdatedAt(LocalDateTime.now());

                conversationRepository.save(conversation);

                saveMessage(
                                conversation.getId(),
                                SenderType.BOT,
                                reply);

                return reply;
        }

        @Override
        public Optional<UUID> resolveClinicIdForPatientPhone(
                        String patientPhone) {

                return conversationRepository
                                .findFirstByPatientPhoneOrderByCreatedAtDesc(patientPhone)
                                .map(Conversation::getClinicId);
        }

        private Conversation createConversation(
                        UUID clinicId,
                        UUID leadId,
                        String patientPhone) {

                Conversation conversation = Conversation.builder()
                                .id(UUID.randomUUID())
                                .clinicId(clinicId)
                                .leadId(leadId)
                                .patientPhone(patientPhone)
                                .state(ConversationState.ASKING_SERVICE)
                                .optedOut(false)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                return conversationRepository.save(conversation);
        }

        private void saveMessage(
                        UUID conversationId,
                        SenderType senderType,
                        String content) {

                Message message = Message.builder()
                                .id(UUID.randomUUID())
                                .conversationId(conversationId)
                                .senderType(senderType)
                                .content(content)
                                .sentAt(LocalDateTime.now())
                                .build();

                messageRepository.save(message);
        }

        private boolean isOptOutKeyword(String text) {

                if (text == null) {
                        return false;
                }

                String normalized = text.trim().toUpperCase();

                return normalized.equals("STOP")
                                || normalized.equals("UNSUBSCRIBE");
        }

        private boolean isGreeting(String text) {

                if (text == null || text.isBlank()) {
                        return false;
                }

                String normalized = text
                                .trim()
                                .toLowerCase()
                                .replaceAll("[^a-zA-Z]", "");

                return normalized.equals("hi")
                                || normalized.equals("hello")
                                || normalized.equals("hey")
                                || normalized.equals("hii")
                                || normalized.equals("hiii")
                                || normalized.equals("heyy")
                                || normalized.equals("goodmorning")
                                || normalized.equals("goodafternoon")
                                || normalized.equals("goodevening");
        }
}
