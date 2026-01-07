package com.mbproyect.campusconnect.service.event;

import com.mbproyect.campusconnect.dto.event.response.EventParticipantResponse;
import com.mbproyect.campusconnect.model.entity.event.EventParticipant;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
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

    Page<EventParticipantResponse> getParticipantsByEvent(
            @NotNull UUID eventId,
            @Min(0) int page,
            @Min(1) @Max(50) int size
    );

    Set<EventParticipantResponse> getParticipantsByEventChatId(
            @NotNull UUID chatId
    );



    EventParticipantResponse subscribeToEvent(@NotNull UUID eventId);

    void cancelEventSubscription(@NotNull UUID participantId);

    EventParticipant getParticipantByEmailAndChatId(UUID chatId, String email);
}
