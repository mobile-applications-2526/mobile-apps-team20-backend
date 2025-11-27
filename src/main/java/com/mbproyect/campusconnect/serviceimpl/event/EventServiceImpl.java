package com.mbproyect.campusconnect.serviceimpl.event;

import com.mbproyect.campusconnect.config.exceptions.event.InvalidDateException;
import com.mbproyect.campusconnect.config.exceptions.user.UserNotFoundException;
import com.mbproyect.campusconnect.dto.event.request.EventRequest;
import com.mbproyect.campusconnect.dto.event.response.EventResponse;
import com.mbproyect.campusconnect.events.contract.event.EventEventsNotifier;
import com.mbproyect.campusconnect.infrastructure.mappers.event.EventBioMapper;
import com.mbproyect.campusconnect.infrastructure.mappers.event.EventMapper;
import com.mbproyect.campusconnect.model.entity.chat.EventChat;
import com.mbproyect.campusconnect.model.entity.event.Event;
import com.mbproyect.campusconnect.model.entity.event.EventBio;
import com.mbproyect.campusconnect.model.entity.event.EventOrganiser;
import com.mbproyect.campusconnect.model.entity.user.User;
import com.mbproyect.campusconnect.model.enums.EventStatus;
import com.mbproyect.campusconnect.model.enums.InterestTag;
import com.mbproyect.campusconnect.infrastructure.repository.event.EventRepository;
import com.mbproyect.campusconnect.service.chat.EventChatService;
import com.mbproyect.campusconnect.service.event.EventOrganiserService;
import com.mbproyect.campusconnect.service.event.EventService;
import com.mbproyect.campusconnect.service.user.UserService;
import com.mbproyect.campusconnect.shared.validation.event.EventValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j  // For showing logs
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventValidator eventValidator;
    private final EventChatService eventChatService;
    private final EventEventsNotifier eventsNotifier;
    private final UserService userService;
    private final EventOrganiserService eventOrganiserService;

    public EventServiceImpl(
            EventRepository eventRepository,
            EventValidator eventValidator,
            EventChatService eventChatService,
            EventEventsNotifier eventsNotifier,
            UserService userService,
            EventOrganiserService eventOrganiserService) {
        this.eventRepository = eventRepository;
        this.eventValidator = eventValidator;
        this.eventChatService = eventChatService;
        this.eventsNotifier = eventsNotifier;
        this.userService = userService;
        this.eventOrganiserService = eventOrganiserService;
    }

    private Page<EventResponse> eventPageToResponse(Page<Event> events) {
        if (events.isEmpty()) return Page.empty();

        return events.map(EventMapper::toResponse);
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
    public Page<EventResponse> getEventsByAnyTag(
            Set<InterestTag> tags,
            int page,
            int size
    ) {
        var pageable = PageRequest.of(page, size);
        String currentUserEmail = userService.getCurrentUser();

        Page<Event> events = eventRepository
                .getEventsByAnyTag(tags, EventStatus.ACTIVE, currentUserEmail, pageable);

        log.info("Returning events with interest tags:  {}", tags);
        return eventPageToResponse(events);
    }

    @Override
    public Page<EventResponse> getEventsByDateAscending(
            LocalDateTime eventDate,
            int size,
            int page
    ) {
        var pageable = PageRequest.of(page, size);
        String currentUserEmail = userService.getCurrentUser();

        Page<Event> events = eventRepository
                .getUpcomingEvents(
                        eventDate, EventStatus.ACTIVE, currentUserEmail, pageable
                );

        if (events.isEmpty()) return Page.empty();

        log.info("Returning events with date:  {}", eventDate);

        return events.map(EventMapper::toResponse);
    }

    @Override
    public Page<EventResponse> getEventsByLocation(
            String city,
            int page,
            int size
    ) {
        var pageable = PageRequest.of(page, size);
        String currentUserEmail = userService.getCurrentUser();

        Page<Event> events = eventRepository
                .findByLocation_City(city, EventStatus.ACTIVE, currentUserEmail, pageable);

        log.info("Returning events in {}", city);
        return eventPageToResponse(events);
    }

    @Override
    public Page<EventResponse> getEventsByLocationAndInterestTag(
            String city,
            Set<InterestTag> tags,
            int page,
            int size
    ) {
        var pageable = PageRequest.of(page, size);
        String currentUserEmail = userService.getCurrentUser();

        Page<Event> events = eventRepository
                .findByLocation_CityAndEventBio_InterestTags(
                        city,
                        tags,
                        EventStatus.ACTIVE,
                        currentUserEmail,
                        pageable
                );

        log.info("Returning events in {}, with interests: {}", city, tags);
        return eventPageToResponse(events);
    }

    @Override
    public Page<EventResponse> getEventsByDateAndInterestTag(
            LocalDateTime startDate,
            Set<InterestTag> tags,
            int page,
            int size
    ) {
        // Updated to include sort by StartDate Ascending
        var pageable = PageRequest.of(page, size, Sort.by("startDate").ascending());
        String currentUserEmail = userService.getCurrentUser();

        Page<Event> events = eventRepository
                .findByStartDateAndEventBio_InterestTags(
                        startDate,
                        tags,
                        EventStatus.ACTIVE,
                        currentUserEmail,
                        pageable
                );

        log.info("Returning events the {}, with interests: {}", startDate, tags);
        return eventPageToResponse(events);
    }

    @Override
    public EventResponse createEvent(EventRequest eventRequest) {
        validateEventDate(
                eventRequest.getStartDate(),
                eventRequest.getEndDate()
        );

        String email = userService.getCurrentUser();
        Event event = EventMapper.fromRequest(eventRequest);

        // Fetch the organiser linked to email
        EventOrganiser organiser = eventOrganiserService.getEventOrganiserByEmail(email, event);
        event.setOrganiser(organiser);
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
        Event event = eventRepository.findByEventId(eventId, EventStatus.ACTIVE);

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
    public Page<EventResponse> getEventsCreatedByCurrentUser(
            int page,
            int size
    ) {
        var pageable = PageRequest.of(page, size);

        String email = userService.getCurrentUser(); // already available pattern
        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No user for token"));

        UUID profileId = user.getUserProfile().getId();
        Page<Event> events = eventRepository
                .findEventsByCreator(profileId, EventStatus.ACTIVE, pageable);

        if (events.isEmpty()) {
            return Page.empty();
        }

        return events.map(EventMapper::toResponse);
    }
}