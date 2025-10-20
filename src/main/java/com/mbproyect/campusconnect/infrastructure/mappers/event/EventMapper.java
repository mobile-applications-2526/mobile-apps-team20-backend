package com.mbproyect.campusconnect.infrastructure.mappers.event;

import com.mbproyect.campusconnect.dto.event.request.EventRequest;
import com.mbproyect.campusconnect.dto.event.response.EventResponse;
import com.mbproyect.campusconnect.model.entity.event.*;
import com.mbproyect.campusconnect.model.enums.EventStatus;

import java.util.HashSet;

public class EventMapper {

    /**
     * Converts an Event entity into an EventResponse DTO.
     */
    public static EventResponse toResponse(Event event) {
        if (event == null) {
            return null;
        }

        EventResponse response = new EventResponse();
        response.setEventId(event.getEventId());
        response.setName(event.getName());
        response.setEventBio(EventBioMapper.toResponse(event.getEventBio()));
        response.setOrganiser(EventOrganiserMapper.toResponse(event.getOrganiser()));
        response.setLocation(event.getLocation());
        response.setStartDate(event.getStartDate());
        response.setEndDate(event.getEndDate());

        return response;
    }

    /**
     * Converts an EventRequest DTO into an Event entity.
     */
    public static Event fromRequest(EventRequest request) {
        if (request == null) {
            return null;
        }

        Event event = new Event();
        event.setName(request.getName());
        event.setEventBio(EventBioMapper.fromRequest(request.getEventBio()));
        event.setOrganiser(EventOrganiserMapper.fromRequest(request.getOrganiser(), new HashSet<>()));
        event.setLocation(request.getLocation());
        event.setStartDate(request.getStarDate());
        event.setEndDate(request.getEndDate());
        event.setEventStatus(EventStatus.ACTIVE); // default status (can be changed later)

        return event;
    }
}
