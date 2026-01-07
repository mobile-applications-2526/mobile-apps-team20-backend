package com.mbproyect.campusconnect.service.event;

import com.mbproyect.campusconnect.model.entity.event.Event;
import com.mbproyect.campusconnect.model.entity.event.EventOrganiser;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public interface EventOrganiserService {
    EventOrganiser getEventOrganiserByEmail(@NotBlank String email, Event event);
    EventOrganiser createEventOrganiser(@NotBlank String email, Event event);
}
