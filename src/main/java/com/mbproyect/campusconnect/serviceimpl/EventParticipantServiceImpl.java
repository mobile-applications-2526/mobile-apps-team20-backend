package com.mbproyect.campusconnect.serviceimpl;

import com.mbproyect.campusconnect.config.exceptions.event.*;
import com.mbproyect.campusconnect.dto.event.response.EventParticipantResponse;
import com.mbproyect.campusconnect.infrastructure.mappers.event.EventParticipantMapper;
import com.mbproyect.campusconnect.infrastructure.repository.event.EventParticipantRepository;
import com.mbproyect.campusconnect.model.entity.event.Event;
import com.mbproyect.campusconnect.model.entity.event.EventParticipant;
import com.mbproyect.campusconnect.model.entity.user.User;
import com.mbproyect.campusconnect.model.enums.EventStatus;
import com.mbproyect.campusconnect.service.EventParticipantService;
import com.mbproyect.campusconnect.shared.validation.EventValidator;
import com.mbproyect.campusconnect.shared.validation.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventParticipantServiceImpl implements EventParticipantService {

    private final EventValidator eventValidator;
    private final EventParticipantRepository eventParticipantRepository;
    private final UserValidator userValidator;

    public EventParticipantServiceImpl(
            EventValidator eventValidator,
            EventParticipantRepository eventParticipantRepository,
            UserValidator userValidator
    ) {
        this.eventValidator = eventValidator;
        this.eventParticipantRepository = eventParticipantRepository;
        this.userValidator = userValidator;
    }

    @Override
    public Set<EventParticipantResponse> getParticipantsByEvent(UUID eventId) {
        // If event do not exist, it throws a not found exception
        Event event = eventValidator.validateEventExists(eventId);

        if (event.getEventStatus().equals(EventStatus.CANCELLED)) {
            throw new EventCancelledException("Event with id " + eventId + "is cancelled");
        }

        Set<EventParticipant> participants = event.getParticipants();

        // Returns an empty set
        if (participants.isEmpty()) return Set.of();

        return participants.stream()
                .map(EventParticipantMapper::toResponse) // Call method reference
                .collect(Collectors.toSet());           // Transforms stream to a set
    }

    @Override
    public EventParticipantResponse subscribeToEvent(UUID eventId, UUID userId) {

        // Fetch user & event data
        User user = userValidator.validateUserExists(userId);
        userValidator.validateUserIsActive(user);
        Optional<EventParticipant> existingParticipant = eventParticipantRepository
                .findEventParticipantByEvent_EventIdAndUserProfile_Id(eventId, user.getUserProfile().getId());

        // Checks if this user has subscription for this event
        if (existingParticipant.isPresent()) {
            throw new ParticipantAlreadyExistsException("A subscription already exists for this event");
        }

        Event event = eventValidator.validateEventExists(eventId);
        eventValidator.validateEventIsActive(event);

        // Create a participant for user
        EventParticipant eventParticipant = new EventParticipant();
        eventParticipant.setEvent(event);
        eventParticipant.setUserProfile(user.getUserProfile());

        eventParticipantRepository.save(eventParticipant);
        log.info("Event participant created with id {}", eventParticipant.getId());

        // TODO: Notify participant

        return EventParticipantMapper
                .toResponse(eventParticipant);
    }

    @Override
    public void cancelEventSubscription(UUID participantId, UUID userId) {

        User user = userValidator.validateUserExists(userId);
        userValidator.validateUserIsActive(user);

        EventParticipant participant = eventParticipantRepository
                .findById(participantId)
                .orElseThrow(() ->  new EventNotFoundException("Participant not found"));

        // Check if who wants to delete the subscription is the current user
        UUID participantUserProfileId = participant.getUserProfile().getId();
        UUID currentUserProfileId = user.getUserProfile().getId();

        if (!participantUserProfileId.equals(currentUserProfileId)) {
            throw new IllegalStateException(
                    "Participant's profile id and current profile's id does not match"
            );
        }

        eventParticipantRepository.delete(participant);
        log.info("Subscription with participant id {} deleted", userId);

        // TODO: Notify participant

    }
}
