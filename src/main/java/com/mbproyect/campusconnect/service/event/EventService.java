package com.mbproyect.campusconnect.service.event;

import com.mbproyect.campusconnect.dto.event.request.EventRequest;
import com.mbproyect.campusconnect.dto.event.response.EventResponse;
import com.mbproyect.campusconnect.model.enums.InterestTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/***
 *  Contract which specify the requirements to implement an event service
 */

@Service
@Validated  // Enables data validation
public interface EventService {

      EventResponse getEventById(@NotNull UUID eventId);

     Set<EventResponse> getEventsByAnyTag(@NotNull Set<InterestTag> tags);

     List<EventResponse> getEventsByDateAscending (@NotNull LocalDateTime eventDate);

     Set<EventResponse> getEventsByLocation (@NotBlank String city);

     EventResponse createEvent (@Valid EventRequest eventRequest);

     EventResponse updateEvent (@Valid EventRequest eventRequest, @NotNull UUID eventId);

     void deleteEvent (@NotNull UUID eventId);
}
