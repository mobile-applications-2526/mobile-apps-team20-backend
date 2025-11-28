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
        SELECT DISTINCT chat
        FROM EventChat chat
        JOIN chat.event event
        JOIN event.organiser organiser
        LEFT JOIN event.participants participant
        WHERE event.eventStatus = :status
        AND (organiser.email = :email OR participant.email = :email)
    """)
    Page<EventChat> getUserChats(
            @Param("email") String email,
            @Param("status") EventStatus status,
            Pageable pageable
    );
}
