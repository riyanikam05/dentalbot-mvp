package com.riya.dentalbot.conversation.repository;

import com.riya.dentalbot.conversation.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    Optional<Conversation> findByLeadId(UUID leadId);

    Optional<Conversation> findFirstByPatientPhoneOrderByCreatedAtDesc(String patientPhone);

}