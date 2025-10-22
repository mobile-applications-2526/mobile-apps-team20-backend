package com.mbproyect.campusconnect.dto.event.response;

import com.mbproyect.campusconnect.dto.chat.response.EventChatResponse;
import com.mbproyect.campusconnect.model.entity.event.EventLocation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {

    private UUID eventId;

    private String name;

    private EventBioResponse eventBio;

    private EventOrganiserResponse organiser;

    private EventLocation location;

    private EventChatResponse chatResponse;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

}
