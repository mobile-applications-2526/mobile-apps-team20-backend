package com.mbproyect.campusconnect.controller.event;

import com.mbproyect.campusconnect.dto.event.request.EventRequest;
import com.mbproyect.campusconnect.dto.event.response.EventParticipantResponse;
import com.mbproyect.campusconnect.dto.event.response.EventResponse;
import com.mbproyect.campusconnect.model.enums.InterestTag;

import com.mbproyect.campusconnect.service.event.EventParticipantService;
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
    public ResponseEntity<Set<EventResponse>> getEventsByAnyTag(@RequestParam Set<InterestTag> tags) {
        Set<EventResponse> responses = eventService.getEventsByAnyTag(tags);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get all events happening on a specific date (sorted ascending)
     * Example: /api/events/by-date?eventDate=2025-10-15
     */
    @GetMapping("/by-date")
    public ResponseEntity<List<EventResponse>> getEventsByDateAscending(@RequestParam LocalDateTime eventDate) {
        List<EventResponse> responses = eventService.getEventsByDateAscending(eventDate);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get all events in a given city
     * Example: /api/events/by-location?city=Leuven
     */
    @GetMapping("/by-location")
    public ResponseEntity<Set<EventResponse>> getEventsByLocation(
            @RequestParam String city
    ) {
        Set<EventResponse> responses = eventService.getEventsByLocation(city);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{eventId}/participants")
    public ResponseEntity<Set<EventParticipantResponse>> getEventParticipants(
            @PathVariable UUID eventId
    ) {
        return ResponseEntity.ok(eventParticipantService.getParticipantsByEvent(eventId));
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody EventRequest eventRequest
    ) {
        EventResponse response = eventService.createEvent(eventRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // This is a temporal version while there is not auth
    @PostMapping("/participants/{participantId}/{userId}")
    public ResponseEntity<EventParticipantResponse> subscribeToEvent(
            @PathVariable UUID participantId, @PathVariable UUID userId
    ) {
        EventParticipantResponse eventParticipantResponse = eventParticipantService
                .subscribeToEvent(participantId, userId);
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

    // This is a temporal version while there is not auth
    @DeleteMapping("/{eventId}/participants/{userProfileId}")
    public ResponseEntity<Void> cancelEventSubscription(
            @PathVariable UUID eventId,
            @PathVariable UUID userProfileId
    ) {
        eventParticipantService.cancelEventSubscription(eventId, userProfileId);
        return ResponseEntity.noContent().build();
    }
}
