package com.mbproyect.campusconnect.service;

import com.mbproyect.campusconnect.dto.event.response.EventParticipantResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Set;
import java.util.UUID;

/***
 *  Contract which specify the requirements to implement an eventParticipant service
 */

@Service
@Validated
public interface EventParticipantService {

    Set<EventParticipantResponse> getParticipantsByEvent(@NotNull UUID eventId);

    EventParticipantResponse subscribeToEvent(@NotNull UUID eventId, @NotNull UUID userId);

    void cancelEventSubscription(@NotNull UUID eventId, @NotNull UUID userId);
}
