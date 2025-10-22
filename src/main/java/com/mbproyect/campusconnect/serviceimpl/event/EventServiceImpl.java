package com.mbproyect.campusconnect.serviceimpl.event;

import com.mbproyect.campusconnect.config.exceptions.event.InvalidDateException;
import com.mbproyect.campusconnect.dto.event.request.EventRequest;
import com.mbproyect.campusconnect.dto.event.response.EventResponse;
import com.mbproyect.campusconnect.infrastructure.mappers.event.EventBioMapper;
import com.mbproyect.campusconnect.infrastructure.mappers.event.EventMapper;
import com.mbproyect.campusconnect.infrastructure.mappers.event.EventOrganiserMapper;
import com.mbproyect.campusconnect.model.entity.event.Event;
import com.mbproyect.campusconnect.model.entity.event.EventBio;
import com.mbproyect.campusconnect.model.enums.EventStatus;
import com.mbproyect.campusconnect.model.enums.InterestTag;
import com.mbproyect.campusconnect.infrastructure.repository.event.EventRepository;
import com.mbproyect.campusconnect.service.chat.EventChatService;
import com.mbproyect.campusconnect.service.event.EventService;
import com.mbproyect.campusconnect.shared.validation.event.EventValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public EventServiceImpl(
            EventRepository eventRepository,
            EventValidator eventValidator,
            EventChatService eventChatService
    ) {
        this.eventRepository = eventRepository;
        this.eventValidator = eventValidator;
        this.eventChatService = eventChatService;
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
    public Set<EventResponse> getEventsByTag(Set<InterestTag> tags) {
        Set<Event> events = eventRepository.getEventsByTags(tags, EventStatus.ACTIVE);

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
                eventRequest.getStarDate(),
                eventRequest.getEndDate()
        );

        Event event = EventMapper.fromRequest(eventRequest);
        eventRepository.save(event);

        log.info("Event created");
        eventChatService.createChat(event);

        return this.getEventById(event.getEventId());
    }

    @Override
    public EventResponse updateEvent(EventRequest eventRequest, UUID eventId) {
        //  Find existing event or throw exception if not found
        //TODO: Check if user is event manager
        Event event = eventValidator.validateEventExists(eventId);
        eventValidator.validateEventIsActive(event);

        boolean hasChanged = false; // Flag to track if any field was modified

        // Compare and update each field if changed
        if (!Objects.equals(event.getName(), eventRequest.getName())) {
            event.setName(eventRequest.getName());
            hasChanged = true;
        }

        EventBio eventBio = EventBioMapper.fromRequest(eventRequest.getEventBio());

        if (!Objects.equals(event.getEventBio(), eventBio)) {
            event.setEventBio(eventBio);
            hasChanged = true;
        }

        if (!Objects.equals(event.getOrganiser(), eventRequest.getOrganiser())) {

            Set<Event> events = new HashSet<>(eventRepository
                    .findAllById(eventRequest.getOrganiser().getEventsIds()));

            event.setOrganiser(
                    EventOrganiserMapper.fromRequest(eventRequest.getOrganiser(), events)
            );
            hasChanged = true;
        }

        if (!Objects.equals(event.getLocation(), eventRequest.getLocation())) {
            event.setLocation(eventRequest.getLocation());
            hasChanged = true;
        }

        if (!Objects.equals(event.getStartDate(), eventRequest.getStarDate())) {
            event.setStartDate(eventRequest.getStarDate());
            hasChanged = true;
        }

        if (!Objects.equals(event.getEndDate(), eventRequest.getEndDate())) {
            event.setStartDate(eventRequest.getEndDate());
            hasChanged = true;
        }

        //  Persist changes only if something was updated
        if (hasChanged) {

            validateEventDate(
                    eventRequest.getStarDate(),
                    eventRequest.getEndDate()
            );

            log.info("Updating event {} due to modified fields", eventId);
            event = eventRepository.save(event);

            // TODO: Notify participants sending email

        } else {
            log.info("No changes detected for event {}", eventId);
        }

        // Convert and return the updated event as a response DTO
        return EventMapper.toResponse(event);
    }

    @Override
    public void deleteEvent(UUID eventId) {
        //TODO: Check if user is event manager
        Event event = eventRepository.findByEventId(eventId);

        // Update event state to cancelled
        event.setEventStatus(EventStatus.CANCELLED);
        log.info("Event with id {} cancelled", eventId);

        // TODO: Notify participants sending email
    }

}
