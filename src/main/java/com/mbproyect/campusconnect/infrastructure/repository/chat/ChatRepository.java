package com.mbproyect.campusconnect.infrastructure.repository.chat;

import com.mbproyect.campusconnect.model.entity.chat.EventChat;
import com.mbproyect.campusconnect.model.enums.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<EventChat, UUID> {
    EventChat findEventChatById(UUID id);

    @Query("""
        SELECT chat FROM EventChat chat
        JOIN chat.event event
        JOIN event.organiser organiser
        LEFT JOIN event.participants participant
        WHERE event.eventStatus = :status
        AND (organiser.email = :email OR participant.email = :email)
        ORDER BY (SELECT MAX(m.sentAt) FROM chat.messages m) DESC
    """)
    Page<EventChat> getUserChats(
            @Param("email") String email,
            @Param("status") EventStatus status,
            Pageable pageable
    );

    @Query("""
    SELECT COUNT(m)
    FROM ChatMessage m
    WHERE m.chat.id = (SELECT ref.chat.id FROM ChatMessage ref WHERE ref.id = :messageId)
    AND m.sentAt > (SELECT ref.sentAt FROM ChatMessage ref WHERE ref.id = :messageId)
""")
    long countMessagesAfter(@Param("messageId") UUID messageId);
}
