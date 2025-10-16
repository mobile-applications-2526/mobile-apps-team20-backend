package com.mbproyect.campusconnect.serviceimpl;

import com.mbproyect.campusconnect.config.exceptions.event.EventNotFoundException;
import com.mbproyect.campusconnect.dto.event.response.EventParticipantResponse;
import com.mbproyect.campusconnect.infrastructure.mappers.event.EventParticipantMapper;
import com.mbproyect.campusconnect.infrastructure.repository.EventRepository;
import com.mbproyect.campusconnect.model.entity.event.Event;
import com.mbproyect.campusconnect.model.entity.event.EventParticipant;
import com.mbproyect.campusconnect.service.EventParticipantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventParticipantServiceImpl implements EventParticipantService {

    private final EventRepository eventRepository;
    // TODO: Implement user repository interface for fetching user to link it with a new participant
    // private UserRepository userRepository;

    public EventParticipantServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Set<EventParticipantResponse> getParticipantsByEvent(UUID eventId) {
        // If event do not exist, it throws a not found exception
        Event event = eventRepository.getEventByEventId(eventId);

        if ( event == null ) {
            log.error("Event with id {} not found", eventId);
            throw new EventNotFoundException("Event not found");
        }

        Set<EventParticipant> participants = event.getParticipants();

        // Returns an empty set
        if (participants.isEmpty()) return Set.of();

        return participants.stream()
                .map(EventParticipantMapper::toResponse) // Call method reference
                .collect(Collectors.toSet());           // Transforms stream to a set
    }

    @Override
    public EventParticipantResponse addParticipant(UUID eventId, UUID userId) {
        return null; // TODO
    }

    @Override
    public void removeParticipant(UUID eventId, UUID userId) {
        // TODO
    }
}
