package com.mbproyect.campusconnect.controller.event;

import com.mbproyect.campusconnect.dto.event.request.EventRequest;
import com.mbproyect.campusconnect.dto.event.response.EventParticipantResponse;
import com.mbproyect.campusconnect.dto.event.response.EventResponse;
import com.mbproyect.campusconnect.model.enums.InterestTag;
import com.mbproyect.campusconnect.service.event.EventParticipantService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mbproyect.campusconnect.service.event.EventService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.*;


@RestController
@RequestMapping("/api/events")
@Slf4j
public class EventController {

    private final EventService eventService;
    private final EventParticipantService eventParticipantService;

    public EventController (EventService eventService, EventParticipantService eventParticipantService) {
        this.eventService = eventService;
        this.eventParticipantService = eventParticipantService;
    }

    /**
     * Get a single event by its ID
     */
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable UUID eventId) {
        EventResponse response = eventService.getEventById(eventId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all events that have the given interest tags
     * Example: /api/events/by-tag?tags=MUSIC&tags=SPORTS
     */
    @GetMapping("/by-any-tag")
    public ResponseEntity<Page<EventResponse>> getEventsByAnyTag(
            @RequestParam Set<InterestTag> tags,
            @RequestParam int page,
            @RequestParam int size
    ) {
        Page<EventResponse> responses = eventService
                .getEventsByAnyTag(tags, page, size);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get all events happening on a specific date (sorted ascending)
     * Example: /api/events/by-date?eventDate=2025-10-15
     */
    @GetMapping("/by-date")
    public ResponseEntity<Page<EventResponse>> getEventsByDateAscending(
            @RequestParam LocalDateTime eventDate,
            @RequestParam int page,
            @RequestParam int size
    ) {
        Page<EventResponse> responses = eventService
                .getEventsByDateAscending(eventDate, page, size);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/by-date-and-interests")
    public ResponseEntity<Page<EventResponse>> getEventsByDateAndInterestTags(
            @RequestParam LocalDateTime eventDate,
            @RequestParam Set<InterestTag> tags,
            @RequestParam int page,
            @RequestParam int size
    ) {
        Page<EventResponse> responses = eventService
                .getEventsByDateAndInterestTag(
                        eventDate,
                        tags,
                        page,
                        size
                );
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/by-location-and-interests")
    public ResponseEntity<Page<EventResponse>> getEventsByLocationAndInterestTags(
            @RequestParam String city,
            @RequestParam Set<InterestTag> tags,
            @RequestParam int page,
            @RequestParam int size
    ) {
        Page<EventResponse> responses = eventService
                .getEventsByLocationAndInterestTag(
                        city,
                        tags,
                        page,
                        size
                );
        return ResponseEntity.ok(responses);
    }

    /**
     * Get all events in a given city
     * Example: /api/events/by-location?city=Leuven
     */
    @GetMapping("/by-location")
    public ResponseEntity<Page<EventResponse>> getEventsByLocation(
            @RequestParam String city,
            @RequestParam int page,
            @RequestParam int size
    ) {
        Page<EventResponse> responses = eventService
                .getEventsByLocation(city, page, size);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{eventId}/participants")
    public ResponseEntity<Page<EventParticipantResponse>> getEventParticipants(
            @PathVariable UUID eventId,
            @RequestParam int page,
            @RequestParam int size
    ) {
        return ResponseEntity.ok(
                eventParticipantService
                        .getParticipantsByEvent(eventId, page, size)
        );
    }

    // TODO: Implement this by looking if the user who sends the token is a eventId participant
//    @GetMapping("/chat/{eventId}/")
//    public ResponseEntity<UUID> getChatId(
//            @PathVariable UUID eventId,
//    ) {
//        return ResponseEntity.ok(eventParticipantService.getChatId(eventId));
//    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody EventRequest eventRequest
    ) {
        EventResponse response = eventService.createEvent(eventRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/participants/{eventId}")
    public ResponseEntity<EventParticipantResponse> subscribeToEvent(
            @PathVariable UUID eventId
    ) {
        EventParticipantResponse eventParticipantResponse = eventParticipantService
                .subscribeToEvent(eventId);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventParticipantResponse);
    }


    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponse> updateEvent(
            @Valid @RequestBody EventRequest eventRequest,
            @PathVariable UUID eventId
    ) {
        EventResponse response = eventService.updateEvent(eventRequest, eventId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable UUID eventId
    ) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{eventId}/participants/")
    public ResponseEntity<Void> cancelEventSubscription(
            @PathVariable UUID eventId
    ) {
        eventParticipantService.cancelEventSubscription(eventId);
        return ResponseEntity.noContent().build();
    }

    /**
    * Get all the events created by a user
    */
    @GetMapping("/userCreatedEvents")
    public ResponseEntity<Page<EventResponse>> getMyEvents(
            @RequestParam int page,
            @RequestParam int size
    ) {
        Page<EventResponse> responses = eventService
                .getEventsCreatedByCurrentUser(page, size);
        return ResponseEntity.ok(responses);
    }
   
}
