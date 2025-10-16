package com.mbproyect.campusconnect.controller.event;

import com.mbproyect.campusconnect.dto.event.request.EventRequest;
import com.mbproyect.campusconnect.dto.event.response.EventResponse;
import com.mbproyect.campusconnect.model.enums.InterestTag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mbproyect.campusconnect.service.EventService;
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

    public EventController (EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Get a single event by its ID
     */
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable UUID eventId) {
        log.info("GET /api/events/{}", eventId);
        EventResponse response = eventService.getEventById(eventId);
        System.out.println(response);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all events that have the given interest tags
     * Example: /api/events/by-tag?tags=MUSIC&tags=SPORTS
     */
    @GetMapping("/by-tag")
    public ResponseEntity<Set<EventResponse>> getEventsByTag(@RequestParam Set<InterestTag> tags) {
        log.info("GET /api/events/by-tag {}", tags);
        Set<EventResponse> responses = eventService.getEventsByTag(tags);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get all events happening on a specific date (sorted ascending)
     * Example: /api/events/by-date?eventDate=2025-10-15
     */
    @GetMapping("/by-date")
    public ResponseEntity<List<EventResponse>> getEventsByDateAscending(@RequestParam LocalDateTime eventDate) {
        log.info("GET /api/events/by-date {}", eventDate);
        List<EventResponse> responses = eventService.getEventsByDateAscending(eventDate);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get all events in a given city
     * Example: /api/events/by-location?city=Leuven
     */
    @GetMapping("/by-location")
    public ResponseEntity<Set<EventResponse>> getEventsByLocation(@RequestParam String city) {
        log.info("GET /api/events/by-location {}", city);
        Set<EventResponse> responses = eventService.getEventsByLocation(city);
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest eventRequest) {
        log.info("POST /api/events - Creating new event");
        EventResponse response = eventService.createEvent(eventRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponse> updateEvent(@Valid @RequestBody EventRequest eventRequest,
                                                     @PathVariable UUID eventId) {
        log.info("PUT /api/events/{} - Updating event", eventId);
        EventResponse response = eventService.updateEvent(eventRequest, eventId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID eventId) {
        log.info("DELETE /api/events/{}", eventId);
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
}
