package com.riya.dentalbot.conversation.service;

import java.util.Optional;
import java.util.UUID;

public interface ConversationService {

    String startConversation(UUID clinicId, String patientPhone);

    String handleIncomingMessage(UUID clinicId, String patientPhone, String messageText);

    Optional<UUID> resolveClinicIdForPatientPhone(String patientPhone);

}