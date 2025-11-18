package com.mbproyect.campusconnect.shared.validation.event;

import com.mbproyect.campusconnect.config.exceptions.event.EventCancelledException;
import com.mbproyect.campusconnect.config.exceptions.event.EventNotFoundException;
import com.mbproyect.campusconnect.infrastructure.repository.event.EventRepository;
import com.mbproyect.campusconnect.model.entity.event.Event;
import com.mbproyect.campusconnect.model.enums.EventStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class EventValidator {

    private final EventRepository eventRepository;

    public EventValidator(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event validateEventExists(UUID eventId) {
        Event event = eventRepository.findByEventId(eventId, EventStatus.ACTIVE);
        if (event == null) {
            log.error("Event with id {} not found", eventId);
            throw new EventNotFoundException("Event with id " + eventId + " not found");
        }
        return event;
    }

    public void validateEventIsActive(Event event) {
        if (event.getEventStatus().equals(EventStatus.ACTIVE)) {
            return;
        }
        throw new EventCancelledException(
                "Event with id " + event.getEventId() + " is cancelled"
        );
    }
}
