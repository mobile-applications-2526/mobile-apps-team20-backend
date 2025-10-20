package com.mbproyect.campusconnect.infrastructure.repository.event;

import com.mbproyect.campusconnect.model.entity.event.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, UUID> {
    Optional<EventParticipant> findEventParticipantByEvent_EventIdAndUserProfile_Id(UUID eventEventId, UUID userProfileId);
}
