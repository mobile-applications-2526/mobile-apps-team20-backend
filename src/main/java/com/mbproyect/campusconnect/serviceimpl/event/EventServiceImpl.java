package com.mbproyect.campusconnect.serviceimpl.event;

import com.mbproyect.campusconnect.config.exceptions.event.InvalidDateException;
import com.mbproyect.campusconnect.config.exceptions.user.UserNotFoundException;
import com.mbproyect.campusconnect.dto.event.request.EventRequest;
import com.mbproyect.campusconnect.dto.event.response.EventResponse;
import com.mbproyect.campusconnect.events.contract.event.EventEventsNotifier;
import com.mbproyect.campusconnect.infrastructure.mappers.event.EventBioMapper;
import com.mbproyect.campusconnect.infrastructure.mappers.event.EventMapper;
import com.mbproyect.campusconnect.infrastructure.mappers.event.EventOrganiserMapper;
import com.mbproyect.campusconnect.model.entity.chat.EventChat;
import com.mbproyect.campusconnect.model.entity.event.Event;
import com.mbproyect.campusconnect.model.entity.event.EventBio;
import com.mbproyect.campusconnect.model.entity.event.EventOrganiser;
import com.mbproyect.campusconnect.model.entity.user.User;
import com.mbproyect.campusconnect.model.enums.EventStatus;
import com.mbproyect.campusconnect.model.enums.InterestTag;
import com.mbproyect.campusconnect.infrastructure.repository.event.EventRepository;
import com.mbproyect.campusconnect.service.auth.TokenStorageService;
import com.mbproyect.campusconnect.service.chat.EventChatService;
import com.mbproyect.campusconnect.service.event.EventService;
import com.mbproyect.campusconnect.service.user.UserService;
import com.mbproyect.campusconnect.shared.validation.event.EventValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j  // For showing logs
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventValidator eventValidator;
    private final EventChatService eventChatService;
    private final EventEventsNotifier eventsNotifier;
    private final UserService userService;

    public EventServiceImpl(
            EventRepository eventRepository,
            EventValidator eventValidator,
            EventChatService eventChatService,
            EventEventsNotifier eventsNotifier,
            UserService userService
    ) {
        this.eventRepository = eventRepository;
        this.eventValidator = eventValidator;
        this.eventChatService = eventChatService;
        this.eventsNotifier = eventsNotifier;
        this.userService = userService;
    }

    private Set<EventResponse> eventSetToResponse (Set<Event> events) {
        if (events.isEmpty()) return Set.of();

        return events.stream()
                .map(EventMapper::toResponse)
                .collect(Collectors.toSet());
    }

    private void validateEventDate(LocalDateTime start, LocalDateTime end) {
        LocalDateTime now = LocalDateTime.now();

        if (start.isBefore(now)) {
            throw new InvalidDateException("Start date must be after now");
        }

        if (end.isBefore(now)) {
            throw new InvalidDateException("End date must be after now");
        }

        // Check if the after date is not before the start
        if (end.isBefore(start)) {
            throw new InvalidDateException("End date must be after start date");
        }

        long duration = Duration.between(start, end).toMinutes();

        if (duration < 5) {
            throw new InvalidDateException("Events must have a minimum of 5 minutes");
        }

        if (duration > 24 * 60) {
            throw new InvalidDateException("Event duration cannot exceed 24 hours");
        }
    }

    @Override
    public EventResponse getEventById(UUID id) {
        Event event = eventValidator.validateEventExists(id);
        eventValidator.validateEventIsActive(event);

        log.info("Returning event with id {}", id);
        return EventMapper.toResponse(event);  // Parse event to event
    }

    @Override
    public Set<EventResponse> getEventsByAnyTag(Set<InterestTag> tags) {
        Set<Event> events = eventRepository.getEventsByAnyTag(tags, EventStatus.ACTIVE);

        log.info("Returning events with interest tags:  {}", tags);
        return eventSetToResponse(events);
    }

    @Override
    public List<EventResponse> getEventsByDateAscending(LocalDateTime eventDate) {
        List<Event> events = eventRepository.getUpcomingEvents(eventDate, EventStatus.ACTIVE);

        if (events.isEmpty()) return List.of();

        log.info("Returning events with date:  {}", eventDate);

        return events.stream()
                .map(EventMapper::toResponse)
                .toList();
    }

    @Override
    public Set<EventResponse> getEventsByLocation(String city) {
        Set<Event> events = eventRepository.findByLocation_City(city);

        log.info("Returning events in {}", city);

        return eventSetToResponse(events);
    }

    @Override
    public EventResponse createEvent(EventRequest eventRequest) {

        validateEventDate(
                eventRequest.getStartDate(),
                eventRequest.getEndDate()
        );

        Event event = EventMapper.fromRequest(eventRequest);
        eventRepository.save(event);

        EventChat chat = eventChatService.createChat(event);
        event.setChat(chat);

        eventRepository.save(event);
        log.info("Event created");

        return this.getEventById(event.getEventId());
    }

    @Override
    public EventResponse updateEvent(EventRequest eventRequest, UUID eventId) {
        Event event = eventValidator.validateEventExists(eventId);
        eventValidator.validateEventIsActive(event);

        // Check if who updates it is the organiser
        userService.validateCurrentUser(
                event.getOrganiser().getEmail()
        );

        List<String> originalValues = new ArrayList<>();
        List<String> changedValues = new ArrayList<>();

        if (!Objects.equals(event.getName(), eventRequest.getName())) {
            originalValues.add("Name: " + event.getName());
            changedValues.add("Name: " + eventRequest.getName());
            event.setName(eventRequest.getName());
        }

        EventBio newBio = EventBioMapper.fromRequest(eventRequest.getEventBio());
        if (!Objects.equals(event.getEventBio(), newBio)) {
            originalValues.add("Description: " + event.getEventBio().getDescription());
            changedValues.add("Description: " + newBio.getDescription());
            event.setEventBio(newBio);
        }

        if (!Objects.equals(event.getLocation(), eventRequest.getLocation())) {
            originalValues.add("Location: " + event.getLocation().toString());
            changedValues.add("Location: " + eventRequest.getLocation().toString());
            event.setLocation(eventRequest.getLocation());
        }

        if (!Objects.equals(event.getStartDate(), eventRequest.getStartDate())) {
            originalValues.add("Start date: " + event.getStartDate());
            changedValues.add("Start date: " + eventRequest.getStartDate());
            event.setStartDate(eventRequest.getStartDate());
        }

        if (!Objects.equals(event.getEndDate(), eventRequest.getEndDate())) {
            originalValues.add("End date: " + event.getEndDate());
            changedValues.add("End date: " + eventRequest.getEndDate());
            event.setEndDate(eventRequest.getEndDate());
        }

        if (!changedValues.isEmpty()) {
            validateEventDate(eventRequest.getStartDate(), eventRequest.getEndDate());

            event = eventRepository.save(event);
            log.info("Updated event {} ({} fields changed)", eventId, changedValues.size());

            // Notify changes
            eventsNotifier.onEventChanged(event, originalValues, changedValues);

        } else {
            log.info("No changes detected for event {}", eventId);
        }

        return EventMapper.toResponse(event);
    }


    @Override
    public void deleteEvent(UUID eventId) {
        Event event = eventRepository.findByEventId(eventId);

        // Validate if current user is the event organiser
        userService.validateCurrentUser(
                event.getOrganiser().getEmail()
        );

        // Update event state to cancelled
        event.setEventStatus(EventStatus.CANCELLED);
        log.info("Event with id {} cancelled", eventId);

        eventsNotifier.onEventCancelled(event);
    }
    @Override
    public List<EventResponse> getEventsCreatedByCurrentUser() {
        String email = userService.getCurrentUser(); // already available pattern
        User user = userService.findUserByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("No user for token"));

        UUID profileId = user.getUserProfile().getId();
        List<Event> events = eventRepository.findEventsByCreator(profileId);

        if (events.isEmpty()) {
            return List.of();
        }

        return events.stream()
            .map(EventMapper::toResponse)
            .toList();
}

}
