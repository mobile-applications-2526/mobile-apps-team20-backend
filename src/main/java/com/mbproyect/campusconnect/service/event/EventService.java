package com.mbproyect.campusconnect.service.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mbproyect.campusconnect.dto.event.request.EventRequest;
import com.mbproyect.campusconnect.dto.event.response.EventResponse;
import com.mbproyect.campusconnect.model.enums.InterestTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/***
 *  Contract which specify the requirements to implement an event service
 */

@Service
@Validated  // Enables data validation
public interface EventService {

      EventResponse getEventById(@NotNull UUID eventId);

     Page<EventResponse> getEventsByAnyTag(
             @NotNull Set<InterestTag> tags,
             @Min(0) int page,
             @Min(1) @Max(50) int size
     );

     Page<EventResponse> getEventsByDateAscending (
             @JsonFormat(pattern ="yyyy-MM-dd'T'HH:mm")
             @NotNull LocalDateTime eventDate,
             @Min(0) int page,
             @Min(1) @Max(50) int size
     );

     Page<EventResponse> getEventsByLocation (
             @NotBlank String city,
             @Min(0) int page,
             @Min(1) @Max(50) int size
     );

     EventResponse createEvent (@Valid EventRequest eventRequest);

     EventResponse updateEvent (@Valid EventRequest eventRequest, @NotNull UUID eventId);

     void deleteEvent (@NotNull UUID eventId);

     Page<EventResponse> getEventsCreatedByCurrentUser(
             @Min(0) int page,
             @Min(1) @Max(50) int size
     );
}
