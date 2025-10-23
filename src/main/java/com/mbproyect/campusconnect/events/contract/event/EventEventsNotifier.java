package com.mbproyect.campusconnect.events.contract.event;

import com.mbproyect.campusconnect.model.entity.event.Event;
import com.mbproyect.campusconnect.model.entity.event.EventParticipant;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface EventEventsNotifier {

    void onEventChanged(
            @NotNull Event event,
            @NotNull List<String> original,
            @NotNull List<String> updated
    );

    void onEventCancelled(@NotNull Event event);

    void onParticipantSubscribed(@NotNull Event event, EventParticipant participantEmail);

    void onParticipantUnsubscribed(@NotNull Event event, EventParticipant participantEmail);

}
