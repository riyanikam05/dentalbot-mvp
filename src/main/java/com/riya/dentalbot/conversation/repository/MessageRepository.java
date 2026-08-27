package com.riya.dentalbot.conversation.repository;

import com.riya.dentalbot.conversation.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findAllByConversationIdOrderBySentAtAsc(UUID conversationId);

}