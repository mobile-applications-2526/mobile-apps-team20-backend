package com.juangomez.campusconnect.controller.event;

import com.juangomez.campusconnect.dto.event.EventRequest;
import com.juangomez.campusconnect.dto.event.EventResponse;
import com.juangomez.campusconnect.model.entity.event.Event;
import com.juangomez.campusconnect.model.entity.event.EventLocation;
import com.juangomez.campusconnect.model.entity.event.EventParticipant;
import com.juangomez.campusconnect.model.enums.EventTag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/event")
public class EventController {

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable int id) {
        return ResponseEntity.ok(new EventResponse());
    }

    @GetMapping("/participants/{id}")
    public ResponseEntity<EventParticipant> getEventParticipants(@PathVariable int id) {
        return ResponseEntity.ok(new EventParticipant());
    }

    @GetMapping
    public ResponseEntity<EventResponse> getEventsByTag(@RequestParam EventTag tag) {
        return ResponseEntity.ok(new EventResponse());
    }

    @GetMapping
    public ResponseEntity<EventResponse> getEventsByDate(@RequestParam Date eventDate) {
        return ResponseEntity.ok(new EventResponse());
    }

    @GetMapping
    public ResponseEntity<EventResponse> getEventsByLocation(@RequestParam EventLocation location) {
        return ResponseEntity.ok(new EventResponse());
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody EventRequest eventRequest) {
        return ResponseEntity.ok(new Event());
    }

    @PutMapping
    public ResponseEntity<EventResponse> updateEvent(@RequestBody EventRequest eventRequest) {
        return ResponseEntity.ok(new EventResponse());
    }


}
