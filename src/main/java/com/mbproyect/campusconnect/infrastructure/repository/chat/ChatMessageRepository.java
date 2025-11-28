package com.mbproyect.campusconnect.infrastructure.repository.chat;

import com.mbproyect.campusconnect.model.entity.chat.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    Page<ChatMessage> findByChat_Id(UUID chatId, Pageable pageable);
}
