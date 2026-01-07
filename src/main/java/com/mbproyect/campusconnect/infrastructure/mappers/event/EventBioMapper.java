package com.mbproyect.campusconnect.infrastructure.mappers.event;

import com.mbproyect.campusconnect.dto.event.request.EventBioRequest;
import com.mbproyect.campusconnect.dto.event.response.EventBioResponse;
import com.mbproyect.campusconnect.model.entity.event.EventBio;

import java.util.HashSet;
import java.util.Set;

public class EventBioMapper {

    /**
     * Converts an EventBio entity into an EventBioResponse DTO.
     */
    public static EventBioResponse toResponse(EventBio eventBio) {
        if (eventBio == null) {
            return null;
        }

        EventBioResponse response = new EventBioResponse();
        response.setId(eventBio.getId());
        response.setDescription(eventBio.getDescription());
        response.setImage(eventBio.getImageUrl());
        response.setInterestTags(
                eventBio.getInterestTags() != null ? new HashSet<>(eventBio.getInterestTags()) : Set.of()
        );

        return response;
    }

    /**
     * Converts an EventBioRequest DTO into an EventBio entity.
     */
    public static EventBio fromRequest(EventBioRequest request) {
        if (request == null) {
            return null;
        }

        EventBio eventBio = new EventBio();
        eventBio.setDescription(request.getDescription());
        eventBio.setImageUrl(request.getImage());
        eventBio.setInterestTags(
                request.getInterestTags() != null ? new HashSet<>(request.getInterestTags()) : new HashSet<>()
        );

        return eventBio;
    }
}
