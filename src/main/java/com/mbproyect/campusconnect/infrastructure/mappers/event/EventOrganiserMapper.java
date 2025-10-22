package com.mbproyect.campusconnect.infrastructure.mappers.event;

import com.mbproyect.campusconnect.dto.event.request.EventOrganiserRequest;
import com.mbproyect.campusconnect.dto.event.response.EventOrganiserResponse;
import com.mbproyect.campusconnect.dto.user.response.UserProfileResponse;
import com.mbproyect.campusconnect.infrastructure.mappers.user.UserProfileMapper;
import com.mbproyect.campusconnect.model.entity.event.Event;
import com.mbproyect.campusconnect.model.entity.event.EventOrganiser;
import com.mbproyect.campusconnect.model.entity.user.UserProfile;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class EventOrganiserMapper {

    /**
     * Converts an EventOrganiser entity into a DTO response.
     */
    public static EventOrganiserResponse toResponse(EventOrganiser organiser) {
        if (organiser == null) {
            return null;
        }

        EventOrganiserResponse response = new EventOrganiserResponse();
        response.setId(organiser.getId());
        response.setUsername(organiser.getUserProfile().getUserName());

        return response;
    }

    /**
     * Converts a DTO request into an EventOrganiser entity.
     * The list of Event entities should be loaded by the service layer.
     */
    public static EventOrganiser fromRequest(EventOrganiserRequest request, Set<Event> existingEvents) {
        if (request == null) {
            return null;
        }

        // Convert the embedded UserProfile
        UserProfile userProfile = UserProfileMapper.fromRequest(request.getUserProfile());

        EventOrganiser organiser = new EventOrganiser();
        organiser.setUserProfile(userProfile);
        organiser.setEvents(existingEvents); // Set of Event entities already loaded from repository

        return organiser;
    }

    /**
     * Helper to convert just the event IDs from an organiser entity.
     */
    public static Set<UUID> extractEventIds(EventOrganiser organiser) {
        if (organiser == null || organiser.getEvents() == null) {
            return Set.of();
        }

        return organiser.getEvents().stream()
                .map(Event::getEventId)
                .collect(Collectors.toSet());
    }
}
