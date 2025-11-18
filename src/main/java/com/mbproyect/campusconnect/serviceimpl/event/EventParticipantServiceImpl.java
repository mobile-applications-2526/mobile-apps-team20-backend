package com.mbproyect.campusconnect.serviceimpl.event;

import com.mbproyect.campusconnect.config.exceptions.event.*;
import com.mbproyect.campusconnect.config.exceptions.user.UserNotFoundException;
import com.mbproyect.campusconnect.dto.event.response.EventParticipantResponse;
import com.mbproyect.campusconnect.events.contract.event.EventEventsNotifier;
import com.mbproyect.campusconnect.infrastructure.mappers.event.EventParticipantMapper;
import com.mbproyect.campusconnect.infrastructure.repository.event.EventParticipantRepository;
import com.mbproyect.campusconnect.infrastructure.repository.user.UserRepository;
import com.mbproyect.campusconnect.model.entity.event.Event;
import com.mbproyect.campusconnect.model.entity.event.EventParticipant;
import com.mbproyect.campusconnect.model.entity.user.User;
import com.mbproyect.campusconnect.service.event.EventParticipantService;
import com.mbproyect.campusconnect.service.user.UserService;
import com.mbproyect.campusconnect.shared.validation.event.EventValidator;
import com.mbproyect.campusconnect.shared.validation.user.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class EventParticipantServiceImpl implements EventParticipantService {

    private final EventValidator eventValidator;
    private final EventParticipantRepository eventParticipantRepository;
    private final UserValidator userValidator;
    private final EventEventsNotifier eventsNotifier;
    private final UserService userService;
    private final UserRepository userRepository;

    public EventParticipantServiceImpl(
            EventValidator eventValidator,
            EventParticipantRepository eventParticipantRepository,
            UserValidator userValidator,
            EventEventsNotifier eventsNotifier,
            UserService userService,
            UserRepository userRepository
    ) {
        this.eventValidator = eventValidator;
        this.eventParticipantRepository = eventParticipantRepository;
        this.userValidator = userValidator;
        this.eventsNotifier = eventsNotifier;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public Page<EventParticipantResponse> getParticipantsByEvent(
            UUID eventId,
            int page, int size
    ) {
        // If event do not exist, it throws a not found exception
        Event event = eventValidator.validateEventExists(eventId);

        if (event == null) {
            throw new EventNotFoundException("Event with id " + eventId + "not found");
        }

        var pageable = PageRequest.of(page, size);
        Page<EventParticipant> participants = eventParticipantRepository
                .findEventParticipantsByEvent(event, pageable);

        // Returns an empty set
        if (participants.isEmpty()) return Page.empty();

        return participants
                .map(EventParticipantMapper::toResponse); // Call method reference
    }

    @Override
    public EventParticipantResponse subscribeToEvent(UUID eventId) {
        String currentUserEmail = userService.getCurrentUser();

        // Fetch user & event data
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found"
                ));
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
        eventParticipant.setEmail(user.getEmail());

        eventParticipantRepository.save(eventParticipant);
        log.info("Event participant created with id {}", eventParticipant.getId());

        eventsNotifier.onParticipantSubscribed(event, eventParticipant);

        return EventParticipantMapper
                .toResponse(eventParticipant);
    }

    @Override
    public void cancelEventSubscription(UUID eventId) {
        String currentUserEmail = userService.getCurrentUser();

        // Fetch user & event data
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found"
                ));
        userValidator.validateUserIsActive(user);

        // Check if who wants to delete the subscription is the current user
        userService.validateCurrentUser(user.getEmail());

        EventParticipant participant = eventParticipantRepository
                .findEventParticipantByEvent_EventIdAndUserProfile_Id(
                        eventId, user.getUserProfile().getId()
                )
                .orElseThrow(() ->  new EventNotFoundException("Participant not found"));

        UUID participantUserProfileId = participant.getUserProfile().getId();
        UUID currentUserProfileId = user.getUserProfile().getId();

        if (!participantUserProfileId.equals(currentUserProfileId)) {
            throw new IllegalStateException(
                    "Participant's profile id and current profile's id does not match"
            );
        }

        eventParticipantRepository.delete(participant);
        log.info("Subscription with participant id {} deleted", participant.getId());

        eventsNotifier.onParticipantUnsubscribed(participant.getEvent(), participant);

    }
}
